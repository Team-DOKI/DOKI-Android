package com.paw.key.presentation.ui.course.walkreview.model

import com.paw.key.domain.entity.reviews.ReviewEntity
import com.paw.key.domain.entity.reviews.ReviewHeaderEntity
import com.paw.key.domain.entity.reviews.SelectedReviewSet

data class WalkSharedReviewUiModel(
    val postTitle: String = "",
    val profileName: String = "",
    val profileImageUrl: String = "",

    val routeId: Int = -1,
    val selectedReviewSets: List<SelectedReviewSetUiModel> = emptyList()
)

data class SelectedReviewSetUiModel(
    val reviewCategoryId: Int,
    val selectedReviewOptionIds: List<Int>
)

fun ReviewHeaderEntity.toUiModel(
    currentRouteId: Int = -1,
    currentSelectedSets: List<SelectedReviewSetUiModel> = emptyList()
): WalkSharedReviewUiModel {
    return WalkSharedReviewUiModel(
        postTitle = this.postTitle,
        profileName = this.reviewerProfile.profileName,
        profileImageUrl = this.reviewerProfile.profileImageUrl,
        routeId = currentRouteId,
        selectedReviewSets = currentSelectedSets
    )
}

fun WalkSharedReviewUiModel.toEntity(): ReviewEntity {
    return ReviewEntity(
        routeId = this.routeId,
        selectedReviewSetList = this.selectedReviewSets.map {
            SelectedReviewSet(
                reviewCategoryId = it.reviewCategoryId,
                selectedReviewOptionIds = it.selectedReviewOptionIds
            )
        }
    )
}