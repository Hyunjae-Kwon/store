package com.zerobase.store.user.controller;

import com.zerobase.store.domain.config.JwtAuthenticationProvider;
import com.zerobase.store.user.domain.AuthResponse;
import com.zerobase.store.user.domain.LogInForm;
import com.zerobase.store.user.domain.model.Partner;
import com.zerobase.store.user.domain.partner.PartnerDto;
import com.zerobase.store.user.domain.partner.RegisterPartner;
import com.zerobase.store.user.service.partner.PartnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/partner")
@RequiredArgsConstructor
public class PartnerController {

    private final PartnerService partnerService;
    private final JwtAuthenticationProvider provider;

    /**
     *
     * 점주 회원가입
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerPartner(
            @RequestBody RegisterPartner.Request request
    ) {
        PartnerDto registeredPartner = partnerService.register(request);

        return ResponseEntity.ok(RegisterPartner.Response.fromDto(registeredPartner));
    }

    /**
     *
     * 점주 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<?> partnerLogin(@RequestBody LogInForm form) {
        Partner partner = partnerService.authenticatePartner(form);

        String token = provider.createToken(
                partner.getPartnerId(),
                partner.getId(),
                partner.getMemberType()
        );

        log.info(
                "[LOGIN] ID={}, ROLE={}",
                partner.getPartnerId(),
                partner.getMemberType()
        );

        return ResponseEntity.ok(
                new AuthResponse(partner.getPartnerId(), token)
        );
    }

}
