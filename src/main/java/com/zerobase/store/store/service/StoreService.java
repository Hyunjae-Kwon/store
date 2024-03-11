package com.zerobase.store.store.service;

import com.zerobase.store.review.domain.review.ReviewDto;
import com.zerobase.store.store.domain.model.Store;
import com.zerobase.store.store.domain.repository.StoreMapperRepository;
import com.zerobase.store.store.domain.repository.StoreRepository;
import com.zerobase.store.store.domain.store.*;
import com.zerobase.store.store.type.PageConst;
import com.zerobase.store.store.type.StoreSortType;
import com.zerobase.store.user.domain.model.Partner;
import com.zerobase.store.user.domain.repository.PartnerRepository;
import com.zerobase.store.user.exception.CustomException;
import com.zerobase.store.user.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class StoreService {

    private final PartnerRepository partnerRepository;
    private final StoreRepository storeRepository;
    private final StoreMapperRepository storeMapperRepository;

    /**
     * 매장 추가하기
     * 1. PARTNER_DOESNT_EXIST : 파트너 존재 확인
     * 2. requestValidate
     * -> PARTNER_ALREADY_HAS_STORE : 해당 파트너가 이미 매장을 등록했는지 확인
     * -> STORE_NAME_ALREADY_EXISTS : 해당 매장명이 이미 존재하는지 확인
     * 3. 해당 파트너 엔티티에 storeId, storeName 저장
     */
    public StoreDto addStore(String partnerId, AddStore.Request request){
        Partner partner = partnerRepository.findByPartnerId(partnerId)
                .orElseThrow(() -> new CustomException(ErrorCode.PARTNER_NOT_FOUND));

        this.requestValidate(partnerId, request.getStoreName());

        Store savedStore = storeRepository.save(
                AddStore.Request.toEntity(request, partnerId)
        );

        partner.setStore(savedStore.getId(), savedStore.getStoreName());
        partnerRepository.save(partner);

        return StoreDto.fromEntity(savedStore);
    }

    private void requestValidate(String partnerId, String storeName){
        if(storeRepository.existsByPartnerId(partnerId)){
            throw new CustomException(ErrorCode.PARTNER_ALREADY_HAS_STORE);
        }else if(storeRepository.existsByStoreName(storeName)){
            throw new CustomException(ErrorCode.STORE_NAME_ALREADY_EXISTS);
        }
    }

    /**
     * 매장 정보 수정
     * @param request
     * @return
     */
    public StoreDto editStore(String partnerId, EditStore.Request request){
        if(!storeRepository.existsByPartnerId(partnerId)){
            throw new CustomException(ErrorCode.PARTNER_NOT_FOUND);
        }

        Store store = storeRepository.findByPartnerId(partnerId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        if(!store.getStoreName().equals(request.getStoreName())
                && storeRepository.existsByStoreName(request.getStoreName())){
            throw new CustomException(ErrorCode.STORE_NAME_ALREADY_EXISTS);
        }

        store.edit(request);

        Store updated = storeRepository.save(store);

        return StoreDto.fromEntity(updated);
    }

    /**
     * 상점 명으로 상점 정보 찾기
     * - STORE_NOT_FOUND : 해당 상점명을 가진 상점이 없을 때
     */
    public StoreDetail findByStoreName(String name){
        Store findStore = storeRepository.findByStoreName(name)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));
        return StoreDetail.fromEntity(findStore);
    }

    /**
     *  상점 명으로 상점 리스트 찾기
     *  SortType : ALL / ALPHABET / RATING / REVIEW_COUNTS / DISTANCE
     *  page : constant로 저장
     */
    public Page<StoreDetail> getStoreListByStoreNameAndSortType(StoreListQuery input, Integer page){

        PageRequest pageRequest = getPageRequestBySortTypeAndPage(input.getSortType(), page);
        Page<Store> findStores = storeRepository.findByStoreNameContaining(input.getStoreName(), pageRequest);

        if(findStores.getNumberOfElements() == 0){
            throw new CustomException(ErrorCode.STORE_NO_SEARCH_RESULT);
        }

        return findStores.map(store -> StoreDetail.fromEntity(store));
    }
    /**
     *  상점 명으로 상점 리스트 찾기
     *  SortType : DISTANCE
     */
    public List<StoreDetail> getStoreListByStoreNameAndDistance(StoreListQuery input, Integer page){
        List<StoreDto> findStores = storeMapperRepository.findByStoreNameOrderByDistance(input, page);
        if(findStores.isEmpty()){
            throw new CustomException(ErrorCode.STORE_NO_SEARCH_RESULT);
        }

        return findStores.stream().map(storeDto -> StoreDetail.fromDto(storeDto)).collect(Collectors.toList());
    }

    private PageRequest getPageRequestBySortTypeAndPage(StoreSortType storeSortType, Integer page){
        PageRequest pageRequest = PageRequest.of(page, PageConst.STORE_LIST_PAGE_SIZE);
        if(storeSortType == StoreSortType.ALPHABET){
            return PageRequest.of(page, PageConst.STORE_LIST_PAGE_SIZE, Sort.by("storeName"));
        }else if(storeSortType == StoreSortType.RATING){
            return PageRequest.of(page, PageConst.STORE_LIST_PAGE_SIZE, Sort.by("rating").descending());
        }else if(storeSortType == StoreSortType.RATING_COUNT){
            return PageRequest.of(page, PageConst.STORE_LIST_PAGE_SIZE, Sort.by("ratingCount").descending());
        }
        return pageRequest;
    }

    /**
     * 리뷰 추가됬을 때, 매장의 리뷰 업데이트
     * @param review : 추가된 리뷰
     */
    public void updateRatingForAddReview(ReviewDto review){
        Store store = storeRepository.findByStoreName(review.getStoreName())
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));
        log.info("Update Store Rating, before update => rating : {}, count : {}", store.getRating(), store.getRatingCount());
        Long ratingCount = store.getRatingCount();
        double rating = getNewRatingForAddReview(store.getRating(), ratingCount, review.getRating());

        store.setRating(rating);
        store.setRatingCount(ratingCount + 1);

        Store saved = storeRepository.save(store);
        log.info("update complete => rating : {}, count : {}", saved.getRating(),saved.getRatingCount());
    }

    /**
     * 리뷰 추가 시 업데이트된 별점 반환
     * @param rating : 매장의 기존 별점
     * @param ratingCount : 매장의 기존 리뷰 수
     * @param reviewRating : 새로운 리뷰의 별점
     * @return : 업데이트 될 매장의 별점
     */
    private double getNewRatingForAddReview(double rating, Long ratingCount, double reviewRating){
        double updatedRating = rating * ((double)ratingCount / (ratingCount + 1)) + reviewRating / (ratingCount + 1);
        log.info("updated : {}", updatedRating);
        return updatedRating;
    }

    /**
     * 리뷰 수정 시 매장 별점 정보 업데이트
     */
    public void updateRatingForEditReview(ReviewDto review, double oldRating){
        Store store = storeRepository.findByStoreName(review.getStoreName())
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        double newRating =
                getNewRatingForEditReview(
                        store.getRating(),
                        store.getRatingCount(),
                        oldRating,
                        review.getRating());

        store.setRating(newRating);

        storeRepository.save(store);
    }

    /**
     * 리뷰 수정 시 업데이트 된 별점 반환
     * @param rating : 기존의 별점
     * @param ratingCount : 기존의 리뷰 수
     * @param reviewOldRating : 기존 리뷰의 별점
     * @param reviewNewRating : 수정된 리뷰의 별점
     * @return 업데이트 될 매장의 별점
     */
    private double getNewRatingForEditReview(double rating, Long ratingCount, double reviewOldRating, double reviewNewRating){
        return rating - (reviewOldRating - reviewNewRating) / ratingCount;
    }

}
