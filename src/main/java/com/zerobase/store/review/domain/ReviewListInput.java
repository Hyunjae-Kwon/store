package com.zerobase.store.review.domain;

import com.zerobase.store.review.type.ReviewSortType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewListInput {

    String storeName;
    ReviewSortType sortType;
}
