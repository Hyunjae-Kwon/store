package com.zerobase.store.store.domain.repository;

import com.zerobase.store.store.domain.model.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    boolean existsByPartnerId(String partnerId);

    boolean existsByStoreName(String storeName);

    Optional<Store> findByStoreName(String storeName);

    Optional<Store> findByPartnerId(String partnerId);

    Page<Store> findByStoreNameContaining(String storeName, Pageable pageable);
}
