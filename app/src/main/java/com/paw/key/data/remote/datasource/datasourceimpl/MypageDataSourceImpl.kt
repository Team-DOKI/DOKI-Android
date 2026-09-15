package com.paw.key.data.remote.datasource.datasourceimpl

import com.paw.key.data.dto.request.mypage.UpdatePetRequestDto
import com.paw.key.data.dto.request.mypage.UpdateUserRequestDto
import com.paw.key.data.dto.response.BaseResponse
import com.paw.key.data.dto.response.mypage.ReviewPostListResponseDto
import com.paw.key.data.dto.response.mypage.RoutePostListResponseDto
import com.paw.key.data.remote.datasource.mypage.MypageDataSource
import com.paw.key.data.service.mypage.MypageService
import retrofit2.Response
import javax.inject.Inject

class MypageDataSourceImpl @Inject constructor(
    private val mypageService: MypageService,
) : MypageDataSource {

    override suspend fun updateUser(body: UpdateUserRequestDto): Response<Unit> =
        mypageService.updateUser(body)

    override suspend fun updatePet(petId: Int, body: UpdatePetRequestDto): Response<Unit> =
        mypageService.updatePet(petId, body)

    override suspend fun getMyRoutes(): BaseResponse<RoutePostListResponseDto> =
        mypageService.getMyRoutes()

    override suspend fun getLikedPosts(): BaseResponse<RoutePostListResponseDto> =
        mypageService.getLikedPosts()

    override suspend fun getMyReviews(): BaseResponse<ReviewPostListResponseDto> =
        mypageService.getMyReviews()
}