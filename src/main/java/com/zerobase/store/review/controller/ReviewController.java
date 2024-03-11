package com.zerobase.store.review.controller;

import com.zerobase.store.review.domain.review.AddReview;
import com.zerobase.store.review.domain.review.EditReview;
import com.zerobase.store.review.domain.review.ReviewDto;
import com.zerobase.store.review.service.ReviewService;
import com.zerobase.store.user.domain.model.Customer;
import com.zerobase.store.user.exception.CustomException;
import com.zerobase.store.user.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰 쓰기
     */
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PostMapping("/add/{reservationId}")
    public ResponseEntity<?> addReview(@PathVariable Long reservationId,
                                       @RequestBody AddReview.Request request,
                                       @AuthenticationPrincipal Customer customer){
        ReviewDto reviewDto = reviewService.addReview(reservationId, customer.getCustomerId(), request);

        return ResponseEntity.ok(AddReview.Response.fromDto(reviewDto));
    }

    /**
     * 내가 쓴 리뷰 리스트 확인
     */
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @GetMapping("/list/{userId}")
    public ResponseEntity<?> reviewList(@PathVariable String userId,
                                        @RequestParam(value = "p", defaultValue = "1") Integer page,
                                        @AuthenticationPrincipal Customer customer){
        if(!customer.getCustomerId().equals(userId)){
            throw new CustomException(ErrorCode.NO_AUTHORITY_ERROR);
        }
        Page<ReviewDto> list = reviewService.reviewListByUserId(userId, page - 1);

        return ResponseEntity.ok(list);
    }

    /**
     * 리뷰 수정
     */
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PutMapping("/review/edit/{reviewId}")
    public ResponseEntity<?> editReview(@PathVariable Long reviewId,
                                        @RequestBody EditReview.Request request,
                                        @AuthenticationPrincipal Customer customer){
        ReviewDto editedReview = reviewService.editReview(reviewId, customer.getCustomerId(), request);

        return ResponseEntity.ok(EditReview.Response.fromDto(editedReview));
    }
}
