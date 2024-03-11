package com.zerobase.store.store.controller;

import com.zerobase.store.review.domain.ReviewListInput;
import com.zerobase.store.review.domain.review.ReviewDetail;
import com.zerobase.store.review.domain.review.ReviewDto;
import com.zerobase.store.review.service.ReviewService;
import com.zerobase.store.store.domain.store.*;
import com.zerobase.store.store.service.StoreService;
import com.zerobase.store.store.type.StoreSortType;
import com.zerobase.store.user.domain.model.Partner;
import com.zerobase.store.user.exception.CustomException;
import com.zerobase.store.user.exception.ErrorCode;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;
    private final ReviewService reviewService;

    /**
     * 매장 등록
     *
     * @param partnerId : 파트너 ID
     * @param request   : 매장 정보 입력
     */
    @ApiOperation(value = "매장 등록", notes = "파트너 ID 하나 당 하나의 매장만 등록할 수 있다.")
    @PreAuthorize("hasRole('ROLE_PARTNER')")
    @PostMapping("/register/{partnerId}")
    public ResponseEntity<?> registerStore(
            @Parameter(name = "partnerId", description = "점주 ID")
            @PathVariable String partnerId,
            @RequestBody AddStore.Request request,
            @AuthenticationPrincipal Partner partner) {
        if (!partnerId.equals(partner.getPartnerId())) {
            log.info("{partnerId} : " + partnerId + ", partner.getPartnerId() : " + partner.getPartnerId());
            throw new CustomException(ErrorCode.STORE_PARTNER_NOT_MATCH);
        }

        StoreDto savedStore = storeService.addStore(partnerId, request);
        return ResponseEntity.ok(AddStore.Response.fromDto(savedStore));
    }

    /**
     * 매장 정보 수정
     *
     * @param partnerId : 파트너 ID
     * @param request   : 수정할 매장 정보 입력
     */
    @ApiOperation(value = "매장 정보 수정")
    @PreAuthorize("hasRole('ROLE_PARTNER')")
    @PutMapping("/edit/{partnerId}")
    public ResponseEntity<?> editStore(
            @Parameter(name = "partnerId", description = "점주 ID")
            @PathVariable String partnerId,
            @RequestBody EditStore.Request request,
            @AuthenticationPrincipal Partner partner) {
        if (!partnerId.equals(partner.getPartnerId())) {
            log.info("{partnerId} : " + partnerId + ", partner.getPartnerId() : " + partner.getPartnerId());
            throw new CustomException(ErrorCode.STORE_PARTNER_NOT_MATCH);
        }
        StoreDto storeDto = storeService.editStore(partnerId, request);
        return ResponseEntity.ok(EditStore.Response.fromDto(storeDto));
    }

    /**
     * 매장 검색
     *
     * @param page  : 페이지
     * @param input : storeName, sortType( ALL, ALPHABET, RATING, RATING_COUNT, DISTANCE)
     */
    @ApiOperation(value = "매장 검색", notes = "@RequestBody에 lat, lnt 값을 포함시키고 sortType=DISTANCE로 설정할 경우 가까운 거리 순으로 조회된다.")
    @GetMapping("/list")
    public ResponseEntity<?> storeList(@RequestParam(value = "p", defaultValue = "1") Integer page,
                                       @RequestBody StoreListQuery input) {
        if (input.getSortType().equals(StoreSortType.DISTANCE)) {
            return ResponseEntity.ok(storeService.getStoreListByStoreNameAndDistance(input, page - 1));
        } else {
            return ResponseEntity.ok(storeService.getStoreListByStoreNameAndSortType(input, page - 1));
        }
    }

    /**
     * 매장 상세
     *
     * @param name : 매장 명
     */
    @GetMapping("/detail")
    public ResponseEntity<?> storeDetail(@RequestParam String name) {
        StoreDetail findStore = storeService.findByStoreName(name);
        return ResponseEntity.ok(findStore);
    }

    /**
     * 매장 별 리뷰 목록 확인
     *
     * @param input : storeName, sort[LATEST(최신 순) / RATING_DESC(별점 높은 순) / RATING_ASC(별점 낮은 순)]
     * @param page  : default=1
     */
    @GetMapping("/review")
    public ResponseEntity<?> reviewListByStoreId(@RequestBody ReviewListInput input,
                                                 @RequestParam(name = "p", defaultValue = "1") Integer page) {
        Page<ReviewDto> list = reviewService.reviewListByStoreName(
                input.getStoreName(), input.getSortType(), page - 1);

        Page<ReviewDetail> responseList = list.map(ReviewDetail::fromDto);

        return ResponseEntity.ok(responseList);
    }
}
