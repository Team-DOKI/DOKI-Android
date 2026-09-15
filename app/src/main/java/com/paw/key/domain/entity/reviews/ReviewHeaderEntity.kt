package com.paw.key.domain.entity.reviews

data class ReviewHeaderEntity(
    val postTitle: String,
    val reviewerProfile: ReviewerProfileEntity
)

data class ReviewerProfileEntity(
    val profileName: String,
    val profileImageUrl: String
)