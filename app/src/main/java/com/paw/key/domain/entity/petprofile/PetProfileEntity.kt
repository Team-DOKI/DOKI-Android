package com.paw.key.domain.entity.petprofile

data class PetProfileEntity(
    val petId: Long,
    val imageUrl: String?,
    val name: String,
    val birth: String,
    val age: String,
    val gender: String,
    val isNeutered: Boolean,
    val breed: String,
    val dbtiName: String?,
    val dbtiDescription: String?,
)