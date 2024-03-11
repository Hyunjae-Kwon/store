package com.zerobase.store.reservation.domain.reservation;

import com.zerobase.store.reservation.domain.model.Reservation;
import com.zerobase.store.reservation.type.ReservationType;
import lombok.Builder;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Data
@Builder
public class ReservationDto {

    private Long id;

    private String customerId;
    private String phone;

    private String partnerId;
    private String storeName;

    private Integer people;

    @Enumerated(EnumType.STRING)
    private ReservationType status;
    private LocalDateTime statusUpdatedAt;


    private LocalDateTime time;

    public static ReservationDto fromEntity(Reservation reservation){
        return ReservationDto.builder()
                .id(reservation.getId())
                .customerId(reservation.getCustomerId())
                .phone(reservation.getPhone())
                .partnerId(reservation.getPartnerId())
                .storeName(reservation.getStoreName())
                .people(reservation.getPeople())
                .status(reservation.getStatus())
                .statusUpdatedAt(reservation.getStatusUpdatedAt())
                .time(reservation.getTime())
                .build();
    }
}
