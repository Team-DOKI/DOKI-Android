package com.paw.key.domain.entity.user

data class PetInfoEntity(
    val name: String,
    val gender: String,
    val birth: String,
    val isNeutered: Boolean,
    val breedId: Int,
    val imageId: Int?
)

data class PetBreedsEntity(
    val breedList: List<PetBreedsItemEntity>
)

data class PetBreedsItemEntity(
    val id: Int,
    val name: String,
)
