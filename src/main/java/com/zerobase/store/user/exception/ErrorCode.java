package com.zerobase.store.user.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    ALREADY_REGISTER_USER(HttpStatus.BAD_REQUEST, "이미 가입된 회원입니다."),
    NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "일치하는 회원이 없습니다."),

    PASSWORD_CHECK_FAIL(HttpStatus.BAD_REQUEST, "패스워드를 확인해주세요."),

    PARTNER_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 파트너가 존재하지 않습니다."),

    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 유저가 존재하지 않습니다."),

    TOKEN_TIME_OUT(HttpStatus.BAD_REQUEST, "토큰이 만료되었습니다."),
    JWT_TOKEN_WRONG_TYPE(HttpStatus.BAD_REQUEST, "JWT 토큰 형식에 문제가 있습니다."),

    ACCESS_DENIED(HttpStatus.BAD_REQUEST, "접근 권한이 없습니다."),
    NO_AUTHORITY_ERROR(HttpStatus.BAD_REQUEST, "권한이 없습니다."),

    STORE_PARTNER_NOT_MATCH(HttpStatus.BAD_REQUEST, "해당 파트너의 매장이 아닙니다."),

    PARTNER_ALREADY_HAS_STORE(HttpStatus.BAD_REQUEST, "해당 파트너의 매장이 이미 존재합니다."),
    STORE_NAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "해당 매장 명이 이미 존재합니다."),
    STORE_NO_SEARCH_RESULT(HttpStatus.BAD_REQUEST, "검색 결과가 없습니다."),
    STORE_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 매장이 존재하지 않습니다."),

    RESERVATION_NOT_FOUND(HttpStatus.BAD_REQUEST, "예약 정보를 찾을 수 없습니다."),
    RESERVATION_IS_ZERO(HttpStatus.BAD_REQUEST, "예약 내역이 없습니다."),
    RESERVATION_UPDATE_AUTH_FAIL(HttpStatus.BAD_REQUEST, "해당 파트너는 해당 예약에 대한 변경 권한이 없습니다."),
    RESERVATION_STATUS_CODE_REQUIRED(HttpStatus.BAD_REQUEST, "예약 상태 코드가 필요합니다." +
            " (REQUESTING, REFUSED, CONFIRM, ARRIVED, USE_COMPLETE, NO_SHOW)"),
    RESERVATION_STATUS_CODE_ILLEGAL_ARGUMENT(HttpStatus.BAD_REQUEST, "해당 상태코드가 존재하지 않습니다." +
            " (REQUESTING, REFUSED, CONFIRM, ARRIVED, USE_COMPLETE, NO_SHOW)"),
    RESERVATION_PHONE_NUMBER_INCORRECT(HttpStatus.BAD_REQUEST, "전화번호 정보가 일치하지 않습니다. 전화번호 뒷 4자리를 다시 입력해주세요."),
    RESERVATION_STATUS_CHECK_ERROR(HttpStatus.BAD_REQUEST, "예약 상태 코드에 문제가 있습니다. 가게에 문의하세요."),
    RESERVATION_TIME_CHECK_ERROR(HttpStatus.BAD_REQUEST, "예약 시간에 문제가 있습니다. 가게에 문의하세요."),

    REVIEW_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "해당 예약은 리뷰를 쓸 수 있는 상태가 아닙니다."),
    REVIEW_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "해당 예약에 대한 리뷰가 이미 존재합니다."),
    REVIEW_RATING_RANGE_ERROR(HttpStatus.BAD_REQUEST, "리뷰 숫자 범위에 문제가 있습니다.(Required : 0 ~ 5)"),
    REVIEW_TEXT_TOO_LONG(HttpStatus.BAD_REQUEST, "텍스트 길이가 너무 깁니다.(200자 이내)"),
    REVIEW_NOT_FOUND(HttpStatus.BAD_REQUEST, "리뷰가 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String detail;
}
