package com.zerobase.store.user.service.partner;

import com.zerobase.store.user.domain.LogInForm;
import com.zerobase.store.user.domain.model.Partner;
import com.zerobase.store.user.domain.partner.PartnerDto;
import com.zerobase.store.user.domain.partner.RegisterPartner;
import com.zerobase.store.user.domain.repository.PartnerRepository;
import com.zerobase.store.user.exception.CustomException;
import com.zerobase.store.user.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class PartnerService {

    private final PartnerRepository partnerRepository;

    public PartnerDto register(RegisterPartner.Request request) {
        if(partnerRepository.existsByPartnerId(request.getPartnerId())) {
            throw new CustomException(ErrorCode.ALREADY_REGISTER_USER);
        }

        Partner partner = partnerRepository.save(
                RegisterPartner.Request.toEntity(request)
        );

        log.info("회원 가입에 성공하였습니다. : {}", partner);

        return PartnerDto.fromEntity(partner);
    }

    public Partner authenticatePartner(LogInForm form) {
        Partner partner = partnerRepository.findByPartnerId(form.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.PARTNER_NOT_FOUND));
        if (!form.getPassword().equals(partner.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_CHECK_FAIL);
        }
        return partner;
    }

}
