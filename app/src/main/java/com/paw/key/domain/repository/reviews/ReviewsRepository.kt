package com.paw.key.domain.repository.reviews

import com.paw.key.domain.entity.reviews.ReviewEntity
import com.paw.key.domain.entity.reviews.ReviewHeaderEntity

interface ReviewsRepository {
    suspend fun postReview(reviewEntity: ReviewEntity, userId: Int): Result<Unit>
    suspend fun getReviewHeader(postId: Int): Result<ReviewHeaderEntity>
}