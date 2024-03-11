package com.zerobase.store.domain.config;

import com.zerobase.store.domain.common.UserType;
import com.zerobase.store.domain.common.UserVo;
import com.zerobase.store.domain.util.Aes256Util;
import com.zerobase.store.user.exception.ErrorCode;
import com.zerobase.store.user.service.CommonService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider {

    private final CommonService commonService;

    private String secretKey = "secretKey";
    private long tokenValidTime = 1000L * 60 * 60 * 24;

    public String createToken(String userPk, Long id, String memberType) {
        Claims claims = Jwts.claims()
                .setSubject(Aes256Util.encrypt(userPk));
//                .setId(Aes256Util.encrypt(id.toString()));
        claims.put("roles", memberType);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(jwtToken);

            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public UserVo getUserVo(String token) {
        Claims c = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();

        return new UserVo(
                Long.valueOf(Objects.requireNonNull(Aes256Util.decrypt(c.getId()))),
                Aes256Util.decrypt(c.getSubject())
        );
    }

    /**
     * token으로 userId(사용자 ID) 찾기
     *
     * @param token
     * @return username
     */
    public String getUserId(String token) {
        return Aes256Util.decrypt(this.parseClaims(token).getSubject());
    }

    /**
     * 토큰이 유효한지 확인
     */
    private Claims parseClaims(String token){
        try{
            return Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody();
        }catch (ExpiredJwtException e) {
            throw new JwtException(ErrorCode.TOKEN_TIME_OUT.getDetail());
        }catch (SignatureException e){
            throw new JwtException(ErrorCode.JWT_TOKEN_WRONG_TYPE.getDetail());
        }
    }

    public Authentication getAuthentication(String token) {
        String userId = this.getUserId(token);
        UserDetails userDetails = commonService.loadUserByUsername(userId);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}
