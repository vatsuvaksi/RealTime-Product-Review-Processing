package com.vatsuvaksi.springbatchdemo.repository;

import com.vatsuvaksi.springbatchdemo.entities.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

    @Query("SELECT pr FROM ProductReview pr ")
    Page<ProductReview> findAllWithPagination(LocalDateTime yesterdayStart, LocalDateTime yesterdayEnd, Pageable pageable);

    @Query("SELECT COUNT(pr) FROM ProductReview pr ")
    long countAllRecords(LocalDateTime yesterdayStart, LocalDateTime yesterdayEnd);
}
