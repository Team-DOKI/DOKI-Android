package com.paw.key.presentation.ui.splash.state

import androidx.compose.runtime.Immutable

@Immutable
data class SplashState(
    val isLoading: Boolean = true,
)

sealed interface SplashSideEffect {
    data object NavigateToLogin : SplashSideEffect

    data object NavigateToHome: SplashSideEffect
    data object NavigateToSignUp: SplashSideEffect
}