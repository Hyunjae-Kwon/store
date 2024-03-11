package com.zerobase.store.user.domain.partner;

import com.zerobase.store.user.domain.model.Partner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PartnerDto {

    private Long id;

    private String partnerId;
    private String password;

    private String name;
    private String phone;

    private Long storeId;
    private String storeName;

    private String memberType; //ROLE_PARTNER

    private LocalDateTime createAt;
    private LocalDateTime updateDt;

    public static PartnerDto fromEntity(Partner partner){
        return PartnerDto.builder()
                .id(partner.getId())
                .partnerId(partner.getPartnerId())
                .password(partner.getPassword())
                .name(partner.getName())
                .phone(partner.getPhone())
                .memberType(partner.getMemberType())
                .createAt(partner.getCreateAt())
                .updateDt(partner.getUpdateAt())
                .build();
    }
}
