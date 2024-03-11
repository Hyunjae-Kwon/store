package com.zerobase.store.review.domain.review;

import com.zerobase.store.reservation.domain.model.Reservation;
import com.zerobase.store.review.domain.model.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class AddReview {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request{
        private double rating;
        private String text;

        public static Review toEntity(Request request, Reservation reservation){
            return Review.builder()
                    .reservationId(reservation.getId())
                    .customerId(reservation.getCustomerId())
                    .storeName(reservation.getStoreName())
                    .rating(request.getRating())
                    .text(request.getText())
                    .createdAt(LocalDateTime.now())
                    .build();
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response{
        private String customerId;
        private String storeName;
        private double rating;
        private String text;
        private LocalDateTime createdAt;

        public static Response fromDto(ReviewDto review){
            return Response.builder()
                    .customerId(review.getCustomerId())
                    .storeName(review.getStoreName())
                    .rating(review.getRating())
                    .text(review.getText())
                    .createdAt(review.getCreatedAt())
                    .build();
        }
    }
}
