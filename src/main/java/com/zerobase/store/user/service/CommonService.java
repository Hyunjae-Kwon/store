package com.zerobase.store.user.service;

import com.zerobase.store.user.domain.repository.CustomerRepository;
import com.zerobase.store.user.domain.repository.PartnerRepository;
import com.zerobase.store.user.exception.CustomException;
import com.zerobase.store.user.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final PartnerRepository partnerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        log.info("Load User => USERNAME : {}", username);
        if (customerRepository.existsByCustomerId(username)) {
            return customerRepository.findByCustomerId(username)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
        } else if (partnerRepository.existsByPartnerId(username)) {
            return partnerRepository.findByPartnerId(username)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
        }
        log.error("AuthService -> loadUserByUsername FAILED");
        return null;
    }
}
