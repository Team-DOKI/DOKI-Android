package com.paw.key.data.dto.response.reviews

import com.paw.key.domain.entity.reviews.ReviewHeaderEntity
import com.paw.key.domain.entity.reviews.ReviewerProfileEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewHeaderResponseDto(
    @SerialName("postTitle")
    val postTitle: String,
    
    @SerialName("reviewerProfile")
    val reviewerProfile: ReviewerProfileDto
) {
    fun toEntity() = ReviewHeaderEntity(
        postTitle = postTitle,
        reviewerProfile = reviewerProfile.toEntity()
    )
}

@Serializable
data class ReviewerProfileDto(
    @SerialName("profileName")
    val profileName: String,
    
    @SerialName("profileImageUrl")
    val profileImageUrl: String
) {
    fun toEntity() = ReviewerProfileEntity(
        profileName = profileName,
        profileImageUrl = profileImageUrl
    )
}