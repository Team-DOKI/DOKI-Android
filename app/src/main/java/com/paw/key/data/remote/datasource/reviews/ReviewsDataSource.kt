package com.paw.key.data.remote.datasource.reviews

import com.paw.key.data.dto.request.reviews.ReviewRequestDto
import com.paw.key.data.service.reviews.ReviewsService
import javax.inject.Inject

class ReviewsDataSource @Inject constructor(
    private val service: ReviewsService
) {
    suspend fun postReview(request: ReviewRequestDto, userId: Int) = service.postReview(userId, request)
    suspend fun getReviewHeader(postId: Int) = service.getReviewHeader(postId)
}