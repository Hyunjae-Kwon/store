package com.zerobase.store.review.domain.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDetail {

    private String customerId;
    private String rating;
    private String text;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ReviewDetail fromDto(ReviewDto reviewDto){
        return ReviewDetail.builder()
                .customerId(reviewDto.getCustomerId())
                .rating(String.format("%.1f", reviewDto.getRating()))
                .text(reviewDto.getText())
                .createdAt(reviewDto.getCreatedAt())
                .updatedAt(reviewDto.getUpdatedAt())
                .build();
    }
}
