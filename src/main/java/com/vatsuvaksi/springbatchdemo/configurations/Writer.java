package com.vatsuvaksi.springbatchdemo.configurations;


import com.vatsuvaksi.springbatchdemo.entities.ProductReview;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Component("writer")
public class Writer implements ItemWriter<ProductReview> {

    private ThreadPoolExecutor executor;
    private int parallelExecutionInWriter;
    private int parallelChunkSizeWriter;

    public void postConstruct() {
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(parallelExecutionInWriter);
    }

    @Override
    public void write(Chunk<? extends ProductReview> chunk) throws Exception {
        //TODO : create subchunks
    }
}
