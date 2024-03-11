package com.zerobase.store.user.domain.customer;

import com.zerobase.store.domain.common.UserType;
import com.zerobase.store.user.domain.model.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class RegisterCustomer {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request{

        private Long id;

        //입력받은 값
        private String customerId;

        private String password;

        private String name;
        private String phone;

        private UserType memberType;    // ROLE_CUSTOMER

        public static Customer toEntity(Request request){
            return Customer.builder()
                    .id(request.getId())
                    .customerId(request.getCustomerId())
                    .password(request.getPassword())
                    .name(request.getName())
                    .phone(request.getPhone())
                    .memberType(UserType.ROLE_CUSTOMER.toString())
                    .createAt(LocalDateTime.now())
                    .build();
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response{

        private Long id;

        //입력받은 값
        private String customerId;

        private String name;
        private String phone;

        //서버 측 설정 값
        private LocalDateTime createAt;
        private String memberType;

        public static Response fromDto(CustomerDto customerDto){
            return Response.builder()
                    .id(customerDto.getId())
                    .customerId(customerDto.getCustomerId())
                    .name(customerDto.getName())
                    .phone(customerDto.getPhone())
                    .memberType(customerDto.getMemberType())
                    .createAt(customerDto.getCreateAt())
                    .build();
        }
    }
}
