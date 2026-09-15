package com.paw.key.domain.entity.reviews

data class ReviewEntity(
    val routeId: Int,
    val selectedReviewSetList: List<SelectedReviewSet>
)

data class SelectedReviewSet(
    val reviewCategoryId: Int,
    val selectedReviewOptionIds: List<Int>
)