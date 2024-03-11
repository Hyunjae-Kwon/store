package com.zerobase.store.store.domain.store;

import com.zerobase.store.store.domain.model.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class StoreDetail {

    private String storeName;
    private String address;
    private String description;
    private String distance;
    private String rating;
    private Long ratingCount;

    public static StoreDetail fromEntity(Store store){
        return StoreDetail.builder()
                .storeName(store.getStoreName())
                .address(store.getAddress())
                .description(store.getDescription())
                .rating(String.format("%.2f", store.getRating()))
                .ratingCount(store.getRatingCount())
                .build();
    }

    public static StoreDetail fromDto(StoreDto store){
        return StoreDetail.builder()
                .storeName(store.getStoreName())
                .address(store.getAddress())
                .description(store.getDescription())
                .distance(String.format("%.3fkm",store.getDistance()))
                .rating(String.format("%.2f", store.getRating()))
                .ratingCount(store.getRatingCount())
                .build();
    }
}
