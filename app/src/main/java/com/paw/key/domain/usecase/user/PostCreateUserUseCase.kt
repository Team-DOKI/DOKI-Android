package com.paw.key.domain.usecase.user

import com.paw.key.core.util.suspendRunCatching
import com.paw.key.domain.entity.image.ImageDomainType
import com.paw.key.domain.entity.image.ImagePresignedEntity
import com.paw.key.domain.entity.user.UserInfoEntity
import com.paw.key.domain.repository.image.ImageRepository
import com.paw.key.domain.repository.localstorage.LocalStorageRepository
import com.paw.key.domain.repository.user.UserRepository
import timber.log.Timber
import javax.inject.Inject

class PostCreateUserUseCase @Inject constructor(
    private val imageRepository: ImageRepository,
    private val userRepository: UserRepository,
    private val localRepository: LocalStorageRepository
) {
    suspend operator fun invoke(
        userInfoEntity: UserInfoEntity,
        petImageUri: String?
    ): Result<Unit> = suspendRunCatching {
        val finalImageId: Int? = if (petImageUri != null) {
            val presignedResult = imageRepository.presignedImage(
                presignedEntity = ImagePresignedEntity(
                    domain = ImageDomainType.PET_PROFILE,
                    contentType = "image/webp"
                )
            ).getOrThrow()

            imageRepository.uploadS3(
                presignedUrl = presignedResult.uploadUrl,
                uriString = petImageUri
            ).getOrThrow()

            val registerImage = imageRepository.registerImage(
                uriString = "${presignedResult.imageUrl}#${petImageUri}",
                domainType = ImageDomainType.PET_PROFILE,
            ).onFailure{Timber.e(it)}.getOrThrow()


            registerImage.imageId
        } else {
            Timber.e("petImageUri is null")
            null
        }

        val finalPetInfo = userInfoEntity.pet.copy(
            imageId = finalImageId
        )

        val finalUserInfo = userInfoEntity.copy(
            pet = finalPetInfo
        )

        val createUser = userRepository.createUser(
            userInfoEntity = finalUserInfo
        ).getOrThrow()

        Timber.e("createUser: $createUser")
        localRepository.saveUserId(userId = createUser.userId)
        localRepository.savePetId(petId = createUser.petId)
    }
}
