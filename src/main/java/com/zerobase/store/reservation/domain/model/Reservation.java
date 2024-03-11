package com.zerobase.store.reservation.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zerobase.store.reservation.type.ReservationType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "RESERVATION")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerId;
    private String phone;

    private String partnerId;
    private String storeName;

    private Integer people;

    @Enumerated(EnumType.STRING)
    private ReservationType status;
    private LocalDateTime statusUpdatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime time;
}
