package com.paw.key.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto (
    @SerialName("accessToken")
    val accessToken: String,
    @SerialName("refreshToken")
    val refreshToken: String,
    @SerialName("isNewUser")
    val isNewUser: Boolean,
    @SerialName("userId")
    val userId: Int,
    @SerialName("petId")
    val petId: Int
)
