package com.paw.key.presentation.ui.mypage.model

import com.paw.key.domain.entity.petprofile.PetProfileEntity

data class PetInfoModel(
    val petId: Int = -1,
    val petImageUrl: String? = "",
    val petName: String = "",
    val petBirthday: String = "",
    val petGender: String = "",
    val petBreed: String = "",
    val petAge: String = "",
    val petNeutered: Boolean = false,
    val petDbtiName: String = "",
    val petDbtiDescription: String = ""
)

fun PetProfileEntity.toUiModel() = PetInfoModel(
    petId = petId.toInt(),
    petImageUrl = imageUrl,
    petName = name,
    petBirthday = birth,
    petGender = gender,
    petBreed = breed,
    petAge = age,
    petNeutered = isNeutered,
    petDbtiName = dbtiName.orEmpty(),
    petDbtiDescription = dbtiDescription.orEmpty()
)
