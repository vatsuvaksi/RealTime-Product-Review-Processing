package com.vatsuvaksi.springbatchdemo.repository;

import com.vatsuvaksi.springbatchdemo.entities.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

    /**
     * Fetches all ProductReview entities created yesterday with pagination support.
     *
     * @param pageable the pagination information
     * @return a page of ProductReview entities created yesterday
     */
    @Query("SELECT pr FROM ProductReview pr WHERE pr.createdAt >= :yesterdayStart AND pr.createdAt < :yesterdayEnd")
    Page<ProductReview> findAllWithPagination(LocalDateTime yesterdayStart, LocalDateTime yesterdayEnd, Pageable pageable);

    /**
     * Retrieves the total count of ProductReview records created yesterday.
     *
     * @return the total count of ProductReview records created yesterday
     */
    @Query("SELECT COUNT(pr) FROM ProductReview pr WHERE pr.createdAt >= :yesterdayStart AND pr.createdAt < :yesterdayEnd")
    long countAllRecords(LocalDateTime yesterdayStart, LocalDateTime yesterdayEnd);
}
