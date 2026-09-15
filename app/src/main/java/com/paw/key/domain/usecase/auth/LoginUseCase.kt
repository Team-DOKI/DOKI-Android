package com.paw.key.domain.usecase.auth

import android.content.Context
import com.paw.key.domain.repository.localstorage.LocalStorageRepository
import com.paw.key.domain.repository.login.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val localStorageRepository: LocalStorageRepository
) {
    suspend fun invokeGoogleLogin(context: Context): Result<Boolean> {
        val deviceId = localStorageRepository.getDeviceId()

        return authRepository.signInWithGoogle(context)
            .mapCatching { idToken ->
                val loginResponse = authRepository.login(idToken, deviceId).getOrThrow()

                localStorageRepository.saveTokens(
                    accessToken = loginResponse.accessToken,
                    refreshToken = loginResponse.refreshToken
                )

                localStorageRepository.savePetId(loginResponse.petId)
                localStorageRepository.saveUserId(loginResponse.userId)

                loginResponse.isNewUser
            }
    }

    suspend fun invokeKakaoLogin(context: Context): Result<Boolean> {
        val deviceId = localStorageRepository.getDeviceId()

        return authRepository.signInWithKakao(context)
            .mapCatching { idToken ->
                val response = authRepository.loginKakao(idToken, deviceId).getOrThrow()

                localStorageRepository.saveTokens(response.accessToken, response.refreshToken)
                localStorageRepository.savePetId(response.petId)
                localStorageRepository.saveUserId(response.userId)

                response.isNewUser
            }
    }
}
