package com.zerobase.store.review.domain.repository;

import com.zerobase.store.review.domain.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByReservationId(Long reservationId);

    Page<Review> findByCustomerIdOrderByCreatedAtDesc(String customerId, Pageable pageable);

    Page<Review> findByStoreNameOrderByCreatedAtDesc(String storeName, Pageable pageable);

    Page<Review> findByStoreNameOrderByRatingDesc(String storeName, Pageable pageable);

    Page<Review> findByStoreNameOrderByRatingAsc(String storeName, Pageable pageable);
}
