package com.paw.key.data.repository.mypage

import com.paw.key.core.util.suspendRunCatching
import com.paw.key.data.dto.request.mypage.UpdatePetRequestDto
import com.paw.key.data.dto.request.mypage.UpdateUserRequestDto
import com.paw.key.data.remote.datasource.mypage.MypageDataSource
import com.paw.key.domain.entity.mypage.ReviewPostEntity
import com.paw.key.domain.entity.mypage.RoutePostEntity
import com.paw.key.domain.entity.mypage.toEntity
import com.paw.key.domain.repository.mypage.MypageRepository
import javax.inject.Inject

class MypageRepositoryImpl @Inject constructor(
    private val dataSource: MypageDataSource,
) : MypageRepository {

    override suspend fun updateUser(name: String, birth: String, gender: String): Result<Unit> =
        suspendRunCatching {
            dataSource.updateUser(UpdateUserRequestDto(name = name, birth = birth, gender = gender))
        }

    override suspend fun updatePet(
        petId: Int,
        name: String,
        birth: String,
        gender: String,
        isNeutered: Boolean,
        breedId: Int,
        imageId: Int,
    ): Result<Unit> = suspendRunCatching {
        dataSource.updatePet(
            petId,
            UpdatePetRequestDto(
                name       = name,
                birth      = birth,
                gender     = gender,
                isNeutered = isNeutered,
                breedId    = breedId,
                imageId    = imageId,
            )
        )
    }

    override suspend fun getMyRoutes(): Result<List<RoutePostEntity>> =
        suspendRunCatching {
            dataSource.getMyRoutes().data?.posts?.map { it.toEntity() } ?: emptyList()
        }

    override suspend fun getLikedPosts(): Result<List<RoutePostEntity>> =
        suspendRunCatching {
            dataSource.getLikedPosts().data?.posts?.map { it.toEntity() } ?: emptyList()
        }

    override suspend fun getMyReviews(): Result<List<ReviewPostEntity>> =
        suspendRunCatching {
            dataSource.getMyReviews().data?.posts?.map { it.toEntity() } ?: emptyList()
        }
}