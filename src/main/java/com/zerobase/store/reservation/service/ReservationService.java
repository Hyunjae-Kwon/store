package com.zerobase.store.reservation.service;

import com.zerobase.store.reservation.domain.model.Reservation;
import com.zerobase.store.reservation.domain.repository.ReservationRepository;
import com.zerobase.store.reservation.domain.reservation.MakeReservation;
import com.zerobase.store.reservation.domain.reservation.ReservationDto;
import com.zerobase.store.reservation.type.ReservationType;
import com.zerobase.store.store.domain.model.Store;
import com.zerobase.store.store.domain.repository.StoreRepository;
import com.zerobase.store.store.type.PageConst;
import com.zerobase.store.user.domain.model.Customer;
import com.zerobase.store.user.domain.repository.CustomerRepository;
import com.zerobase.store.user.exception.CustomException;
import com.zerobase.store.user.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CustomerRepository customerRepository;
    private final StoreRepository storeRepository;

    /**
     * 유저 - 매장 예약
     * @param request : userId, storeName, people(인원 수)
     * @return
     */
    public ReservationDto makeReservation(MakeReservation.Request request){
        Reservation reservation = makeReservationEntity(request);
        Reservation saved = reservationRepository.save(reservation);
        log.info("reservation id : {}", saved.getId());
        return ReservationDto.fromEntity(reservation);
    }

    /**
     * 매장 예약 Request를 바탕으로 ReservationEntity 생성
     */
    private Reservation makeReservationEntity(MakeReservation.Request request){
        Customer customer = customerRepository.findByCustomerId(request.getCustomerId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Store store = storeRepository.findByStoreName(request.getStoreName())
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        LocalDateTime reservationTime = LocalDateTime.of(request.getDate(), request.getTime());

        return Reservation.builder()
                .customerId(customer.getCustomerId())
                .phone(customer.getPhone())
                .partnerId(store.getPartnerId())
                .storeName(store.getStoreName())
                .people(request.getPeople())
                .status(ReservationType.REQUESTING)
                .statusUpdatedAt(LocalDateTime.now())
                .time(reservationTime)
                .build();
    }

    /**
     * 유저/파트너 - 예약 상세 정보
     */
    public ReservationDto reservationDetail(Long id, String username){
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if (!this.validateReservationAccessAuthority(username, reservation)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        return ReservationDto.fromEntity(reservation);
    }

    /**
     * userDetails의 userId가 reservationDto의 userId 또는 partnerId와 일치하는지 확인
     */
    private boolean validateReservationAccessAuthority(String userId, Reservation reservation) {
        if (reservation.getCustomerId().equals(userId)) {
            log.info("CustomerID : {}, 예약 내역 확인", userId);
            return true;
        } else if (reservation.getPartnerId().equals(userId)) {
            log.info("PartnerId : {}, 예약 내역 확인", userId);
            return true;
        }
        return false;
    }

    /**
     * 파트너 - partner ID로 예약 내역 확인
     * @param partnerId
     * @param page
     * sort : 최신순
     */
    public Page<ReservationDto> listForPartner(String partnerId, Integer page){
        Page<Reservation> reservations =
                reservationRepository.findByPartnerIdOrderByTimeDesc(
                        partnerId,
                        PageRequest.of(page, PageConst.RESERVATION_LIST_PAGE_SIZE)
                );

        if(reservations.getSize() == 0){
            throw new CustomException(ErrorCode.RESERVATION_IS_ZERO);
        }
        return reservations.map(reservation -> ReservationDto.fromEntity(reservation));
    }

    /**
     * 파트너 - partner ID와 ReservationStatus로 내역 확인
     * sort : 시간 빠른 순
     */
    public Page<ReservationDto> listForPartnerByStatus(String partnerId, ReservationType status, Integer page){

        Page<Reservation> reservations =
                reservationRepository.findByPartnerIdAndStatusOrderByTime(
                        partnerId,
                        status,
                        PageRequest.of(page, PageConst.RESERVATION_LIST_PAGE_SIZE)
                );

        if(reservations.getSize() == 0){
            throw new CustomException(ErrorCode.RESERVATION_IS_ZERO);
        }
        return reservations.map(reservation -> ReservationDto.fromEntity(reservation));
    }

    /**
     * 파트너 - partner ID와 예약 날짜로 내역 확인
     * sort : 시간 빠른 순
     */
    public Page<ReservationDto> listForPartnerByDate(String partnerId, LocalDate date, Integer page){

        Page<Reservation> reservations =
                reservationRepository.findByPartnerIdAndTimeBetweenOrderByTime(
                        partnerId,
                        LocalDateTime.of(date, LocalTime.MIN),
                        LocalDateTime.of(date, LocalTime.MAX),
                        PageRequest.of(page, PageConst.RESERVATION_LIST_PAGE_SIZE)
                );

        if(reservations.getSize() == 0){
            throw new CustomException(ErrorCode.RESERVATION_IS_ZERO);
        }
        return reservations.map(reservation -> ReservationDto.fromEntity(reservation));
    }

    /**
     * 파트너 - partner ID와 예약 상태(status), 예약 날짜(time)로 내역 확인
     * sort : 시간 빠른 순
     */
    public Page<ReservationDto> listForPartnerByStatusAndDate(String partnerId,ReservationType status, LocalDate date, Integer page){

        Page<Reservation> reservations =
                reservationRepository.findByPartnerIdAndStatusAndTimeBetweenOrderByTime(
                        partnerId,
                        status,
                        LocalDateTime.of(date, LocalTime.MIN),
                        LocalDateTime.of(date, LocalTime.MAX),
                        PageRequest.of(page, PageConst.RESERVATION_LIST_PAGE_SIZE)
                );

        if(reservations.getSize() == 0){
            throw new CustomException(ErrorCode.RESERVATION_IS_ZERO);
        }
        return reservations.map(reservation -> ReservationDto.fromEntity(reservation));
    }


    /**
     * 파트너 - 예약 상태 변경
     * @param reservationId
     * @param status
     * @param partnerId
     */
    public void changeReservationStatus(String partnerId, Long reservationId, ReservationType status){
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if(!reservation.getPartnerId().equals(partnerId)){
            throw new CustomException(ErrorCode.RESERVATION_UPDATE_AUTH_FAIL);
        }
        reservation.setStatus(status);
        reservation.setStatusUpdatedAt(LocalDateTime.now());
        reservationRepository.save(reservation);
    }



    /**
     * 유저 - user ID로 예약 내역 확인
     * sort : 시간 빠른 순
     */
    public Page<ReservationDto> listForUser(String userId, Integer page){
        Page<Reservation> reservations =
                reservationRepository.findByCustomerIdOrderByTimeDesc(
                        userId,
                        PageRequest.of(page, PageConst.RESERVATION_LIST_PAGE_SIZE)
                );

        if(reservations.getSize() == 0){
            throw new CustomException(ErrorCode.RESERVATION_IS_ZERO);
        }
        return reservations.map(reservation -> ReservationDto.fromEntity(reservation));
    }

    /**
     * 유저 - userId와 ReservationStatus로 내역 확인
     * sort : 시간 빠른 순
     */
    public Page<ReservationDto> listForUserByStatus(String userId, Integer page, ReservationType status){

        Page<Reservation> reservations =
                reservationRepository.findByCustomerIdAndStatusOrderByTime(
                        userId,
                        status,
                        PageRequest.of(page, PageConst.RESERVATION_LIST_PAGE_SIZE)
                );

        if(reservations.getSize() == 0){
            throw new CustomException(ErrorCode.RESERVATION_IS_ZERO);
        }
        return reservations.map(reservation -> ReservationDto.fromEntity(reservation));
    }

    /**
     * 도착 확인
     * @param reservationId
     * @return boolean result
     */
    public ReservationDto arrivedCheck(Long reservationId, String inputPhoneNumberLast4){
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
        validate(reservation, inputPhoneNumberLast4);
        reservation.setStatus(ReservationType.ARRIVED);
        reservation.setStatusUpdatedAt(LocalDateTime.now());
        reservationRepository.save(reservation);
        return ReservationDto.fromEntity(reservation);
    }

    /**
     * 도착 확인 validation
     * 1. 전화번호 뒷 4자리 확인
     * 2. 예약 상태가 CONFIRM인지 확인
     * 3. 예약 상태에 맞게 왔는지 확인
     */
    private void validate(Reservation reservation, String inputPhoneNumberLast4){
        String rightPhoneNumberLast4 = reservation.getPhone().substring(7);

        if(!rightPhoneNumberLast4.equals(inputPhoneNumberLast4)){
            throw new CustomException(ErrorCode.RESERVATION_PHONE_NUMBER_INCORRECT);
        }else if(!reservation.getStatus().equals(ReservationType.CONFIRM)){
            throw new CustomException(ErrorCode.RESERVATION_STATUS_CHECK_ERROR);
        }else if(LocalDateTime.now().isAfter(reservation.getTime().minusMinutes(10L))){
            throw new CustomException(ErrorCode.RESERVATION_TIME_CHECK_ERROR);
            //(현재시간) > (예약 시간 - 10분) => 10분 전에 도착하지 못함.
        }
    }
}
