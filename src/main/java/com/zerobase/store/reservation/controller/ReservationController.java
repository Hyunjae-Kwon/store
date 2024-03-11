package com.zerobase.store.reservation.controller;

import com.zerobase.store.reservation.domain.reservation.*;
import com.zerobase.store.reservation.service.ReservationService;
import com.zerobase.store.reservation.type.ReservationType;
import com.zerobase.store.user.domain.model.Customer;
import com.zerobase.store.user.domain.model.Partner;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 예약 상세 정보 보기
     * - @AuthenticationPricipal로 로그인 된 유저 정보를 받아서 유저 or 파트너에게 정보 주기
     * @param userDetails : 로그인 정보
     */
    @GetMapping("/detail/{reservationId}")
    public ResponseEntity<?> reservationDetail(@PathVariable Long reservationId,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        ReservationDto reservationDto =
                reservationService.reservationDetail(reservationId, userDetails.getUsername());
        return ResponseEntity.ok(reservationDto);
    }

    /**
     * 예약 요청
     */
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PostMapping("/customer/request")
    public ResponseEntity<?> reservation(@RequestBody MakeReservation.Request request,
                                         @AuthenticationPrincipal Customer customer){
        request.setCustomerId(customer.getCustomerId());
        ReservationDto reservationDto = reservationService.makeReservation(request);

        return ResponseEntity.ok(MakeReservation.Response.fromDto(reservationDto));
    }

    /**
     * 유저 - 예약 내역 모두 보기
     * 정렬 : 최신 순
     * @param customer : 로그인 된 유저
     */
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @GetMapping("/customer/list")
    public ResponseEntity<?> reservationListForUser(@RequestParam(value = "p", defaultValue = "1") Integer page,
                                                    @AuthenticationPrincipal Customer customer){
        Page<ReservationDto> reservationList = reservationService.listForUser(customer.getCustomerId(), page - 1);
        return ResponseEntity.ok(reservationList);
    }

    /**
     * 유저 - 예약 내역 모두 보기 (status별)
     * @param status : 예약 진행 상태 ReservationStatus(enum)
     * @param customer : 로그인 된 유저
     */
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @GetMapping("/customer/list/{status}")
    public ResponseEntity<?> reservationListForUserByStatus(@PathVariable ReservationType status,
                                                            @RequestParam(value = "p", defaultValue = "1") Integer page,
                                                            @AuthenticationPrincipal Customer customer){
        Page<ReservationDto> reservationList =
                reservationService.listForUserByStatus(customer.getCustomerId(), page - 1, status);

        return ResponseEntity.ok(reservationList);
    }

    /**
     * 매장 도착 확인
     * @param input (reservationId, phoneNumberLast4)
     */
    @ApiOperation(value = "매장 도착 확인", notes = "매장에 도착해서 예약 ID와 전화 번호 뒷 4자리로 도착 확인을 할 수 있다. ")
    @PostMapping("/customer/arrived")
    public ResponseEntity<?> arrivedHandling(@RequestBody CustomerArrivedInput input){
        ReservationDto reservationDto = reservationService.arrivedCheck(input.getReservationId(), input.getPhoneNumberLast4());

        return ResponseEntity.ok(new CustomerArrivedComplete(reservationDto));
    }

    /**
     * 파트너 - 예약 내역 모두 보기
     * @param status 예약 진행 상태 ReservationStatus (required = false)
     * @param date 예약 날짜 LocalDate (required = false)
     * @param page 페이지 (default = 1)
     * @param partner 로그인 된 파트너
     */
    @PreAuthorize("hasRole('ROLE_PARTNER')")
    @GetMapping("/partner/list")
    public ResponseEntity<?> reservationListForPartner(@RequestParam(required = false) String status,
                                                       @RequestParam(required = false)@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                                       @RequestParam(value = "p", defaultValue = "1") Integer page,
                                                       @AuthenticationPrincipal Partner partner){
        Page<ReservationDto> reservationList;

        if(Objects.isNull(status) && Objects.isNull(date)){
            reservationList = reservationService.listForPartner(partner.getPartnerId(), page - 1);
        }else if(Objects.nonNull(status) && Objects.isNull(date)){
            reservationList = reservationService.listForPartnerByStatus(
                    partner.getPartnerId(), ReservationType.of(status), page - 1);
        }else if(Objects.nonNull(date) && Objects.isNull(status)){
            reservationList = reservationService.listForPartnerByDate(
                    partner.getPartnerId(), date, page - 1);
        }else{
            reservationList = reservationService.listForPartnerByStatusAndDate(
                    partner.getPartnerId(), ReservationType.of(status), date, page - 1);
        }

        return ResponseEntity.ok(reservationList);
    }


    /**
     * 파트너 - 예약 상태 변경
     * @param id : reservationId
     * @param input : 변경하고자하는 상태
     * @param partner : 로그인 된 파트너
     */
    @ApiOperation(value = "예약 상태 변경", notes = "status : REQUESTING, REFUSED, CONFIRM, ARRIVED, USE_COMPLETE, NO_SHOW")
    @PreAuthorize("hasRole('ROLE_PARTNER')")
    @PutMapping("/partner/reservation/{reservationId}")
    public ResponseEntity<?> changeReservationStatus(@PathVariable("reservationId") Long id,
                                                     @RequestBody ChangeReservationInput input,
                                                     @AuthenticationPrincipal Partner partner){
        reservationService.changeReservationStatus(partner.getPartnerId(), id, ReservationType.of(input.getStatus()));

        return ResponseEntity.ok(reservationService.reservationDetail(id, partner.getPartnerId()));
    }
}
