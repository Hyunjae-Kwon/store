package com.zerobase.store.user.domain.partner;

import com.zerobase.store.domain.common.UserType;
import com.zerobase.store.user.domain.model.Customer;
import com.zerobase.store.user.domain.model.Partner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class RegisterPartner {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request{

        private Long id;

        //입력받은 값
        private String partnerId;

        private String password;

        private String name;
        private String phone;

        private UserType memberType;    // ROLE_CUSTOMER

        public static Partner toEntity(Request request){
            return Partner.builder()
                    .id(request.getId())
                    .partnerId(request.getPartnerId())
                    .password(request.getPassword())
                    .name(request.getName())
                    .phone(request.getPhone())
                    .memberType(UserType.ROLE_PARTNER.toString())
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
        private String partnerId;

        private String name;
        private String phone;

        //서버 측 설정 값
        private LocalDateTime createAt;
        private String memberType;

        public static Response fromDto(PartnerDto partnerDto){
            return Response.builder()
                    .id(partnerDto.getId())
                    .partnerId(partnerDto.getPartnerId())
                    .name(partnerDto.getName())
                    .phone(partnerDto.getPhone())
                    .memberType(partnerDto.getMemberType())
                    .createAt(partnerDto.getCreateAt())
                    .build();
        }
    }
}
