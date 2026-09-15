package com.paw.key.domain.repository.mypage

import com.paw.key.domain.entity.mypage.ReviewPostEntity
import com.paw.key.domain.entity.mypage.RoutePostEntity

interface MypageRepository {
    suspend fun updateUser(name: String, birth: String, gender: String): Result<Unit>
    suspend fun updatePet(petId: Int, name: String, birth: String, gender: String, isNeutered: Boolean, breedId: Int, imageId: Int): Result<Unit>
    suspend fun getMyRoutes(): Result<List<RoutePostEntity>>
    suspend fun getLikedPosts(): Result<List<RoutePostEntity>>
    suspend fun getMyReviews(): Result<List<ReviewPostEntity>>
}