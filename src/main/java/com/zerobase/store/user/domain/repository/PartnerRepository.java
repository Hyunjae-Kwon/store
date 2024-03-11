package com.zerobase.store.user.domain.repository;

import com.zerobase.store.user.domain.model.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {

    boolean existsByIdAndPartnerId(Long id, String partnerId);

    boolean existsByPartnerId(String partnerId);

    Optional<Partner> findByPartnerId(String partnerId);

    Optional<Partner> findByIdAndPartnerId(Long id, String partnerId);
}
