package com.zerobase.store.reservation.type;

import com.zerobase.store.user.exception.CustomException;
import com.zerobase.store.user.exception.ErrorCode;
import org.springframework.util.StringUtils;

public enum ReservationType {

    /**
     * 예약 요청 중
     */
    REQUESTING,

    /**
     * 예약 거절 상태
     */
    REFUSED,

    /**
     * 예약 승인 상태
     */
    CONFIRM,

    /**
     * 도착 확인
     */
    ARRIVED,

    /**
     * 이용 완료
     */
    USE_COMPLETE,

    /**
     * 예약 승인 후 이용하지 않음(no-show)
     */
    NO_SHOW;

    public static ReservationType of(String status){
        status = status.toUpperCase();

        if(!StringUtils.hasText(status)){
            throw new CustomException(ErrorCode.RESERVATION_STATUS_CODE_REQUIRED);
        }
        for(ReservationType rs : ReservationType.values()){
            if(rs.toString().equals(status)){
                return rs;
            }
        }

        throw new CustomException(ErrorCode.RESERVATION_STATUS_CODE_ILLEGAL_ARGUMENT);

    }
}
