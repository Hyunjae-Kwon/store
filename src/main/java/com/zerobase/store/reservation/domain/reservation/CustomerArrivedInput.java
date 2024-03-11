package com.zerobase.store.reservation.domain.reservation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerArrivedInput {
    private Long reservationId;
    private String phoneNumberLast4;
}
