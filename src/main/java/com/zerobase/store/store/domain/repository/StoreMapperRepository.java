package com.zerobase.store.store.domain.repository;

import com.zerobase.store.store.domain.store.StoreDto;
import com.zerobase.store.store.domain.store.StoreListQuery;
import com.zerobase.store.store.type.PageConst;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class StoreMapperRepository {

    private final StoreMapper storeMapper;

    public List<StoreDto> findByStoreNameOrderByDistance(StoreListQuery input, Integer page) {
        Integer size = PageConst.STORE_LIST_PAGE_SIZE;

        return storeMapper.findStoreListOrderByDistance(
                input.getStoreName(),
                input.getLat(),
                input.getLnt(),
                size * page,
                size
        );
    }
}
