package com.zerobase.store.user.service.customer;

import com.zerobase.store.user.domain.LogInForm;
import com.zerobase.store.user.domain.customer.CustomerDto;
import com.zerobase.store.user.domain.customer.RegisterCustomer;
import com.zerobase.store.user.domain.model.Customer;
import com.zerobase.store.user.domain.repository.CustomerRepository;
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
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerDto register(RegisterCustomer.Request request) {
        if(customerRepository.existsByCustomerId(request.getCustomerId())) {
            throw new CustomException(ErrorCode.ALREADY_REGISTER_USER);
        }

        Customer customer = customerRepository.save(
                RegisterCustomer.Request.toEntity(request)
        );

        log.info("회원 가입에 성공하였습니다. : {}", customer);

        return CustomerDto.fromEntity(customer);
    }

    public Customer authenticateCustomer(LogInForm form) {
        Customer customer = customerRepository.findByCustomerId(form.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if (!form.getPassword().equals(customer.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_CHECK_FAIL);
        }
        return customer;
    }
}
