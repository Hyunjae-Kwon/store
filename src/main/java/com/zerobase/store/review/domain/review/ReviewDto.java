package com.zerobase.store.review.domain.review;

import com.zerobase.store.review.domain.model.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewDto {

    private Long id;
    private Long reservationId;
    private String customerId;
    private String storeName;
    private double rating;
    private String text;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ReviewDto fromEntity(Review review){
        return ReviewDto.builder()
                .id(review.getId())
                .reservationId(review.getReservationId())
                .customerId(review.getCustomerId())
                .storeName(review.getStoreName())
                .rating(review.getRating())
                .text(review.getText())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
