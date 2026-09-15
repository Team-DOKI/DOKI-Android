package com.paw.key.data.dto.request.reviews

import com.paw.key.domain.entity.reviews.ReviewEntity
import com.paw.key.domain.entity.reviews.SelectedReviewSet
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewRequestDto(
    @SerialName("routeId")
    val routeId: Int,
    
    @SerialName("selectedReviewSetList")
    val selectedReviewSetList: List<SelectedReviewSetDto>
)

@Serializable
data class SelectedReviewSetDto(
    @SerialName("reviewCategoryId")
    val reviewCategoryId: Int,
    
    @SerialName("selectedReviewOptionIds")
    val selectedReviewOptionIds: List<Int>
)

fun ReviewEntity.toDto() = ReviewRequestDto(
    routeId = routeId,
    selectedReviewSetList = selectedReviewSetList.map { it.toDto() }
)

fun SelectedReviewSet.toDto() = SelectedReviewSetDto(
    reviewCategoryId = reviewCategoryId,
    selectedReviewOptionIds = selectedReviewOptionIds
)