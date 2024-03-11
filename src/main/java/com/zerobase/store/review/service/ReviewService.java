package com.zerobase.store.review.service;

import com.zerobase.store.reservation.domain.model.Reservation;
import com.zerobase.store.reservation.domain.repository.ReservationRepository;
import com.zerobase.store.reservation.type.ReservationType;
import com.zerobase.store.review.domain.model.Review;
import com.zerobase.store.review.domain.repository.ReviewRepository;
import com.zerobase.store.review.domain.review.AddReview;
import com.zerobase.store.review.domain.review.EditReview;
import com.zerobase.store.review.domain.review.ReviewDto;
import com.zerobase.store.review.type.ReviewSortType;
import com.zerobase.store.store.service.StoreService;
import com.zerobase.store.store.type.PageConst;
import com.zerobase.store.user.domain.repository.CustomerRepository;
import com.zerobase.store.user.exception.CustomException;
import com.zerobase.store.user.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CustomerRepository customerRepository;
    private final ReservationRepository reservationRepository;

    private final StoreService storeService;

    /**
     * 리뷰 쓰기
     * @param reservationId : 예약 id
     * @param userId : 유저 id
     * 해당 예약의 userId와 로그인 유저의 userId 일치 확인
     */
    @Transactional
    public ReviewDto addReview(Long reservationId, String userId, AddReview.Request request){
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        validateReviewAvailable(reservation, userId);
        validateReviewDetail(request.getRating(), request.getText());

        Review review = AddReview.Request.toEntity(request, reservation);
        Review savedReview = reviewRepository.save(review);

        storeService.updateRatingForAddReview(ReviewDto.fromEntity(savedReview));//매장 리뷰 업데이트

        return ReviewDto.fromEntity(review);
    }

    /**
     * 해당 리뷰를 쓸 권한이 있는지 검증
     */
    private void validateReviewAvailable(Reservation reservation, String userId){
        //유저 로그인 상태가 아닌 경우
        if(!customerRepository.existsByCustomerId(userId)){
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        // 해당 예약의 userID와 리뷰를 쓴 유저 ID가 일치하지 않는 경우
        if(!reservation.getCustomerId().equals(userId)){
            throw new CustomException(ErrorCode.NO_AUTHORITY_ERROR);
        }
        // 해당 예약에 대한 리뷰가 이미 존재하는 경우
        if(reviewRepository.existsByReservationId(reservation.getId())){
            throw new CustomException(ErrorCode.REVIEW_ALREADY_EXIST);
        }
        // 해당 예약이 USE_COMPLETE 상태가 아니라서 리뷰를 쓸 수 없는 경우
        if(!reservation.getStatus().equals(ReservationType.USE_COMPLETE)){
            throw new CustomException(ErrorCode.REVIEW_NOT_AVAILABLE);
        }
    }

    /**
     * 리뷰의 별점 범위, 텍스트 길이 검증
     */
    private void validateReviewDetail(double rating, String text){
        if(rating > 5 || rating < 0){
            throw new CustomException(ErrorCode.REVIEW_RATING_RANGE_ERROR);
        }
        if(text.length() > 200){
            throw new CustomException(ErrorCode.REVIEW_TEXT_TOO_LONG);
        }
    }

    /**
     * 리뷰 리스트 조회 by userId
     * sort : 최신 순
     */
    public Page<ReviewDto> reviewListByUserId(String userId, Integer page){
        PageRequest pageRequest = PageRequest.of(page, PageConst.REVIEW_LIST_PAGE_SIZE);
        Page<Review> findList = reviewRepository.findByCustomerIdOrderByCreatedAtDesc(userId, pageRequest);

        if(findList.getSize() == 0){
            throw new CustomException(ErrorCode.REVIEW_NOT_FOUND);
        }
        return findList.map(review -> ReviewDto.fromEntity(review));
    }

    /**
     * 리뷰 리스트 조회 by storeName
     * sort : 최신 순, 별점 높은 순, 별점 낮은 순
     */
    public Page<ReviewDto> reviewListByStoreName(String storeName, ReviewSortType sortType, Integer page){
        PageRequest pageRequest = PageRequest.of(page, PageConst.REVIEW_LIST_PAGE_SIZE);
        Page<Review> findList;

        if(sortType.equals(ReviewSortType.RATING_DESC)){//별점 높은 순
            findList = reviewRepository.findByStoreNameOrderByRatingDesc(storeName, pageRequest);
        } else if (sortType.equals(ReviewSortType.RATING_ASC)){ //별점 낮은 순
            findList = reviewRepository.findByStoreNameOrderByRatingAsc(storeName, pageRequest);
        }else{//default : 최신 순
            findList = reviewRepository.findByStoreNameOrderByCreatedAtDesc(storeName, pageRequest);
        }

        if(findList.getNumberOfElements() == 0){
            throw new CustomException(ErrorCode.REVIEW_NOT_FOUND);
        }
        return findList.map(review -> ReviewDto.fromEntity(review));
    }

    /**
     * 리뷰 수정
     */
    public ReviewDto editReview(Long reviewId, String userId, EditReview.Request request){
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        double oldRating = review.getRating();

        if(!review.getCustomerId().equals(userId)){
            throw new CustomException(ErrorCode.NO_AUTHORITY_ERROR);
        }

        validateReviewDetail(request.getRating(), request.getText());

        review.setRating(request.getRating());
        review.setText(request.getText());
        Review savedReview = reviewRepository.save(review);
        ReviewDto editedReview = ReviewDto.fromEntity(savedReview);;

        storeService.updateRatingForEditReview(editedReview, oldRating);

        return editedReview;
    }
}
