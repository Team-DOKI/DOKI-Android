package com.paw.key.data.service.reviews

import com.paw.key.data.dto.request.reviews.ReviewRequestDto
import com.paw.key.data.dto.response.BaseResponse
import com.paw.key.data.dto.response.reviews.ReviewHeaderResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ReviewsService {
    // 공유 루트 종료 후 리뷰
    @POST("reviews")
    suspend fun postReview(
        @Query("userId") userId : Int,
        @Body request: ReviewRequestDto
    ): Response<Unit>

    // 공유 루트 리뷰의 상단 조회 시
    @GET("reviews/{postId}/review-header")
    suspend fun getReviewHeader(
        @Path("postId") postId: Int
    ): BaseResponse<ReviewHeaderResponseDto>
}