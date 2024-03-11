package com.zerobase.store.store.domain.store;

import com.zerobase.store.store.type.StoreSortType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreListQuery {

    private String storeName;
    private StoreSortType sortType;

    private double lat;
    private double lnt;
}
