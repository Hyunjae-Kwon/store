package com.zerobase.store.store.domain.store;

import com.zerobase.store.store.domain.model.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class AddStore {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request{
        private String storeName;
        private String address;
        private String description;

        private double lat;
        private double lnt;

        public static Store toEntity(Request request, String partnerId){
            return Store.builder()
                    .partnerId(partnerId)
                    .storeName(request.getStoreName())
                    .address(request.getAddress())
                    .description(request.getDescription())
                    .lat(request.getLat())
                    .lnt(request.getLnt())
                    .createAt(LocalDateTime.now())
                    .rating(0.0)
                    .ratingCount(0L)
                    .build();
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response{
        private String partnerId;

        private String storeName;
        private String storeAddr;
        private String text;
        private LocalDateTime createAt;

        public static Response fromDto(StoreDto storeDto){
            return Response.builder()
                    .partnerId(storeDto.getPartnerId())
                    .storeName(storeDto.getStoreName())
                    .storeAddr(storeDto.getAddress())
                    .text(storeDto.getDescription())
                    .createAt(storeDto.getCreateAt())
                    .build();
        }
    }
}
