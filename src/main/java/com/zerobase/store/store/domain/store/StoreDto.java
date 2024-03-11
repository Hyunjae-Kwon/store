package com.zerobase.store.store.domain.store;

import com.zerobase.store.store.domain.model.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreDto {

    private Long id;

    private String partnerId;

    private String storeName;
    private String address;
    private String description;

    private double lat;
    private double lnt;
    private double distance;

    private double rating;
    private Long ratingCount;

    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public static StoreDto fromEntity(Store store){
        return StoreDto.builder()
                .id(store.getId())
                .partnerId(store.getPartnerId())
                .storeName(store.getStoreName())
                .address(store.getAddress())
                .description(store.getDescription())
                .lat(store.getLat())
                .lnt(store.getLnt())
                .rating(store.getRating())
                .ratingCount(store.getRatingCount())
                .createAt(store.getCreateAt())
                .updateAt(store.getUpdateAt())
                .build();
    }
}
