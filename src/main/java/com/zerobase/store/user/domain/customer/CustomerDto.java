package com.zerobase.store.user.domain.customer;

import com.zerobase.store.user.domain.model.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerDto {

    private Long id;

    private String customerId;
    private String password;

    private String name;
    private String phone;

    private String memberType; //ROLE_CUSTOMER

    private LocalDateTime createAt;
    private LocalDateTime updateDt;

    public static CustomerDto fromEntity(Customer customer){
        return CustomerDto.builder()
                .id(customer.getId())
                .customerId(customer.getCustomerId())
                .password(customer.getPassword())
                .name(customer.getName())
                .phone(customer.getPhone())
                .memberType(customer.getMemberType())
                .createAt(customer.getCreateAt())
                .updateDt(customer.getUpdateAt())
                .build();
    }
}
