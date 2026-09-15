package com.paw.key.data.dto.request.user

import com.paw.key.domain.entity.user.PetInfoEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PetInfoRequestDto(
    @SerialName("name")
    val name: String,
    @SerialName("gender")
    val gender: String,
    @SerialName("birth")
    val birth: String,
    @SerialName("isNeutered")
    val isNeutered: Boolean,
    @SerialName("breedId")
    val breedId: Int,
    @SerialName("imageId")
    val imageId: Int?
) {
    fun toEntity() = PetInfoEntity(
        name = name,
        gender = gender,
        birth = birth,
        isNeutered = isNeutered,
        breedId = breedId,
        imageId = imageId
    )
}

fun PetInfoEntity.toDto() = PetInfoRequestDto(
    name = name,
    gender = gender,
    birth = birth,
    isNeutered = isNeutered,
    breedId = breedId,
    imageId = imageId
)