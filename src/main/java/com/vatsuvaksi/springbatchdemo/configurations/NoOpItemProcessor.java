package com.vatsuvaksi.springbatchdemo.configurations;

import com.vatsuvaksi.springbatchdemo.entities.ProductReview;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component("processor")
public class NoOpItemProcessor implements ItemProcessor<ProductReview, ProductReview> {
    @Override
    public ProductReview process(ProductReview item) throws Exception {
        return item;
    }
}
