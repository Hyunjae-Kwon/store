package com.zerobase.store.user.domain.repository;

import com.zerobase.store.user.domain.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByIdAndCustomerId(Long id, String customerId);

    boolean existsByCustomerId(String customerId);

    Optional<Customer> findByCustomerId(String customerId);

    Optional<Customer> findByIdAndCustomerId(Long id, String customerId);
}
