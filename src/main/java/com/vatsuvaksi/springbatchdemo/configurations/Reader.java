package com.vatsuvaksi.springbatchdemo.configurations;

import com.vatsuvaksi.springbatchdemo.entities.ProductReview;
import com.vatsuvaksi.springbatchdemo.repository.ProductReviewRepository;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Iterator;

@Component("reader")
public class Reader implements ItemReader<ProductReview> {

    @Autowired
    private ProductReviewRepository repository;
    private Iterator<ProductReview> currentBatch;
    private int pageNumber;
    private int pageSize;
    private final LocalDateTime yesterdayStart;
    private final LocalDateTime yesterdayEnd;


    public Reader() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        this.yesterdayStart = yesterday.atStartOfDay();
        this.yesterdayEnd = yesterday.atTime(LocalTime.MAX);

    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution){
        ExecutionContext executionContext = stepExecution.getExecutionContext();
        this.pageNumber = executionContext.getInt("pageNumber");
        this.pageSize = executionContext.getInt("pageSize");
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<ProductReview> page = repository.findAllWithPagination(yesterdayStart, yesterdayEnd, pageRequest);
        currentBatch = page.hasContent() ? page.getContent().iterator() : null;
    }

    @Override
    public ProductReview read() throws Exception {
        // Return the next item from the batch, or null if no more data
        if (currentBatch != null && currentBatch.hasNext()) {
            return currentBatch.next();
        }

        return null;
    }
}

