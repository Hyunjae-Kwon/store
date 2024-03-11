package com.zerobase.store.user.controller;

import com.zerobase.store.domain.config.JwtAuthenticationProvider;
import com.zerobase.store.user.domain.AuthResponse;
import com.zerobase.store.user.domain.LogInForm;
import com.zerobase.store.user.domain.customer.CustomerDto;
import com.zerobase.store.user.domain.customer.RegisterCustomer;
import com.zerobase.store.user.domain.model.Customer;
import com.zerobase.store.user.service.customer.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final JwtAuthenticationProvider provider;

    /**
     *
     * 고객 회원가입
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(
            @RequestBody RegisterCustomer.Request request
    ) {
        CustomerDto registeredUser = customerService.register(request);
        return ResponseEntity.ok(
                RegisterCustomer.Response.fromDto(registeredUser)
        );
    }

    /**
     *
     * 고객 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<?> customerLogin(@RequestBody LogInForm form) {
        Customer customer = customerService.authenticateCustomer(form);

        String token = provider.createToken(
                customer.getCustomerId(),
                customer.getId(),
                customer.getMemberType()
        );

        log.info(
                "[LOGIN] ID={}, ROLE={}",
                customer.getCustomerId(),
                customer.getMemberType()
        );

        return ResponseEntity.ok(
                new AuthResponse(customer.getCustomerId(), token)
        );
    }
}
