package com.zerobase.store.review.domain.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class EditReview {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request{
        private double rating;
        private String text;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response{
        private String storeName;
        private String customerId;
        private double rating;
        private String text;

        public static Response fromDto(ReviewDto review){
            return Response.builder()
                    .storeName(review.getStoreName())
                    .customerId(review.getCustomerId())
                    .rating(review.getRating())
                    .text(review.getText())
                    .build();
        }
    }
}
