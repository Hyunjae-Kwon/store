package com.zerobase.store.reservation.domain.repository;

import com.zerobase.store.reservation.domain.model.Reservation;
import com.zerobase.store.reservation.type.ReservationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Page<Reservation> findByCustomerIdOrderByTimeDesc(String customerId, Pageable pageable);

    Page<Reservation> findByCustomerIdAndStatusOrderByTime(String customerId, ReservationType status, Pageable pageable);

    Page<Reservation> findByPartnerIdOrderByTimeDesc(String partnerId, Pageable pageable);

    Page<Reservation> findByPartnerIdAndStatusOrderByTime(
            String partnerId, ReservationType status, Pageable pageable);

    Page<Reservation> findByPartnerIdAndTimeBetweenOrderByTime(
            String partnerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Reservation> findByPartnerIdAndStatusAndTimeBetweenOrderByTime(
            String partnerId, ReservationType status, LocalDateTime start, LocalDateTime end, Pageable pageable);
}
