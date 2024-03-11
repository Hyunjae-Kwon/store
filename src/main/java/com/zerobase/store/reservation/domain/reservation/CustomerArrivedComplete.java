package com.zerobase.store.reservation.domain.reservation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerArrivedComplete {

    private Long reservationId;
    private String storeName;
    private String customerId;
    private LocalDateTime arrivedTime;
    private LocalDateTime reservationTime;

    public CustomerArrivedComplete(ReservationDto reservation){
        this.reservationId = reservation.getId();
        this.storeName = reservation.getStoreName();
        this.customerId = reservation.getCustomerId();
        this.arrivedTime = LocalDateTime.now();
        this.reservationTime = reservation.getTime();
    }
}
