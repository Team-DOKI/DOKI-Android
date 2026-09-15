package com.paw.key.data.repositoryimpl.reviews

import com.paw.key.core.util.suspendRunCatching
import com.paw.key.data.dto.request.reviews.toDto
import com.paw.key.data.remote.datasource.reviews.ReviewsDataSource
import com.paw.key.domain.entity.reviews.ReviewEntity
import com.paw.key.domain.entity.reviews.ReviewHeaderEntity
import com.paw.key.domain.repository.reviews.ReviewsRepository
import javax.inject.Inject

class ReviewsRepositoryImpl @Inject constructor(
    private val dataSource: ReviewsDataSource
) : ReviewsRepository {
    override suspend fun postReview(reviewEntity: ReviewEntity, userId: Int): Result<Unit> = suspendRunCatching {
        dataSource.postReview(reviewEntity.toDto(), userId)
    }

    override suspend fun getReviewHeader(postId: Int): Result<ReviewHeaderEntity> = suspendRunCatching{
        dataSource.getReviewHeader(postId)
            .data
            .toEntity()
    }
}