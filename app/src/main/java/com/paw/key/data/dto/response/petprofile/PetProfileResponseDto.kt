package com.paw.key.data.dto.response.petprofile

import com.paw.key.domain.entity.petprofile.PetProfileEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PetProfileResponseDto(
    @SerialName("petId")
    val petId: Long,
    @SerialName("imageUrl")
    val imageUrl: String?,
    @SerialName("name")
    val name: String,
    @SerialName("birth")
    val birth: String,
    @SerialName("age")
    val age: String,
    @SerialName("gender")
    val gender: String,
    @SerialName("isNeutered")
    val isNeutered: Boolean,
    @SerialName("breed")
    val breed: String,
    @SerialName("dbtiName")
    val dbtiName: String?,
    @SerialName("dbtiDescription")
    val dbtiDescription: String?
) {
    fun toEntity() = PetProfileEntity(
        petId = petId,
        imageUrl = imageUrl,
        name = name,
        birth = birth,
        age = age,
        gender = gender,
        isNeutered = isNeutered,
        breed = breed,
        dbtiName = dbtiName,
        dbtiDescription = dbtiDescription
    )
}