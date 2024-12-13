package com.vatsuvaksi.springbatchdemo.configurations;

import com.vatsuvaksi.springbatchdemo.repository.ProductReviewRepository;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Component("partitioner")
public class ChunkPartitioner implements Partitioner {

    private final ProductReviewRepository repository;
    private final LocalDateTime yesterdayStart;
    private final LocalDateTime yesterdayEnd;
    private final int chunkSize = 5000;

    @Autowired
    public ChunkPartitioner(ProductReviewRepository repository) {
        this.repository = repository;
        LocalDate yesterday = LocalDate.now().minusDays(1);
        this.yesterdayStart = yesterday.atStartOfDay();
        this.yesterdayEnd = yesterday.atTime(LocalTime.MAX);
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        long totalRecords = repository.countAllRecords(yesterdayStart,yesterdayEnd);
        System.out.println("Total Records Satisfying the condition " + totalRecords);
        int numberOfPartitions = (int) Math.ceil((double) totalRecords / chunkSize);
        Map<String, ExecutionContext> partitions = new HashMap<>();

        for (int i = 0; i < numberOfPartitions; i++) {
            ExecutionContext context = new ExecutionContext();
            context.putInt("pageNumber", i);
            context.putInt("pageSize", chunkSize);
            partitions.put("partition" + i, context);
            System.out.println("Creating partition -" + i + " with pageNumber + " + i + " chunkSize " + chunkSize);
        }

        return partitions;
    }
}
