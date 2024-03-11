package com.zerobase.store.domain.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private final JwtAuthenticationProvider provider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain
    ) throws ServletException, IOException {

        String token = request.getHeader("X-AUTH-TOKEN");

        log.info("TOKEN : " + token);

        if(StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring("Bearer ".length());
        }

        // 토큰 유효성 검증
        if(StringUtils.hasText(token) && provider.validateToken(token)) {
            Authentication auth = provider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        } else {
            log.info("토큰 유효성 검증을 실패하였습니다.");
        }

        chain.doFilter(request, response);
    }
}
