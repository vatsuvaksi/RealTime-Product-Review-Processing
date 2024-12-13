package com.vatsuvaksi.springbatchdemo.configurations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vatsuvaksi.springbatchdemo.entities.ProductReview;
import com.vatsuvaksi.springbatchdemo.redis.RedisService;
import jakarta.annotation.PostConstruct;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Component("writer")
@StepScope
public class Writer implements ItemWriter<ProductReview> {

    private ThreadPoolExecutor executor;
    private final int parallelChunkSizeWriter = 2500;
    private final WebClient webClient;
    private final RedisService redisServer;


    @Autowired
    public Writer(WebClient.Builder webClientBuilder, RedisService redisServer) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:3031").build();
        this.redisServer = redisServer;
    }

    @PostConstruct
    public void postConstruct() {
        int parallelExecutionInWriter = 2;
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(parallelExecutionInWriter);
    }

    @Override
    public void write(Chunk<? extends ProductReview> chunk) throws Exception {
        List<? extends ProductReview> reviews = chunk.getItems();

        // Step 1: Create subchunks of size parallelChunkSizeWriter
        List<List<ProductReview>> subChunks = createSubChunks(reviews, parallelChunkSizeWriter);

        // Step 2: Process subchunks in parallel using executor
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (List<ProductReview> subChunk : subChunks) {
            futures.add(CompletableFuture.runAsync(() -> processSubChunk(subChunk), executor));
        }

        // Wait for all parallel tasks to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private List<List<ProductReview>> createSubChunks(List<? extends ProductReview> reviews, int chunkSize) {
        List<List<ProductReview>> subChunks = new ArrayList<>();
        for (int i = 0; i < reviews.size(); i += chunkSize) {
            subChunks.add(new ArrayList<>(reviews.subList(i, Math.min(i + chunkSize, reviews.size()))));
        }
        return subChunks;
    }

    private void processSubChunk(List<ProductReview> subChunk) {
        // Collect all API calls into a list of Monos
        List<Mono<Void>> tasks = new ArrayList<>();

        for (ProductReview review : subChunk) {
            Mono<Void> task = callGetApi(review)
                    .flatMap(response -> {
                        storeInRedis(review, response);
                        return Mono.empty(); // Return a Mono<Void> to chain further processing if needed
                    });
            tasks.add(task);
        }

        // Combine all Monos and wait for completion
        Mono.when(tasks).block(); // This will wait for all tasks in this subchunk to complete
    }


    private Mono<String> callGetApi(ProductReview review) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/endpoint")
                        .queryParam("id", review.getProductId())
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .doOnCancel(() -> System.out.println("Request was canceled"));
    }

    private void storeInRedis(ProductReview review, String apiResponse)  {

        try {
            redisServer.saveToRedis(String.valueOf(review.getReviewId()), apiResponse, review);
        } catch (JsonProcessingException e) {
            System.out.println("Redis push failed" + e.getMessage());
        }
        System.out.println("Storing in Redis: " + review + " with response: " + apiResponse);
    }
}