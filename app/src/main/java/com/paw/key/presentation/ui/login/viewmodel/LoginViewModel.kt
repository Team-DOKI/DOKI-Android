package com.paw.key.presentation.ui.login.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paw.key.domain.repository.localstorage.LocalStorageRepository
import com.paw.key.domain.usecase.auth.LoginUseCase
import com.paw.key.presentation.ui.login.state.LoginSideEffect
import com.paw.key.presentation.ui.login.state.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val localStorageRepository: LocalStorageRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState>
        get() = _state.asStateFlow()

    private val _sideEffect = MutableStateFlow<LoginSideEffect?>(null)
    val sideEffect: StateFlow<LoginSideEffect?>
        get() = _sideEffect.asStateFlow()

    fun onGoogleSignIn(
        context: Context,
    ) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            loginUseCase.invokeGoogleLogin(context)
                .onSuccess { isNewUser ->
                    localStorageRepository.saveUserProvider("GOOGLE")

                    val petId = localStorageRepository.getPetId()

                    if (isNewUser || petId == -1) {
                        _sideEffect.emit(LoginSideEffect.NavigateToSignUp)
                    } else {
                        _sideEffect.emit(LoginSideEffect.NavigateToHome)
                    }

                    _state.update { it.copy(isLoading = false) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false) }
                    _sideEffect.emit(LoginSideEffect.ShowSnackBar("로그인에 실패했습니다\n잠시 후에 다시 시도해주세요"))
                    Timber.e(e, "Google sign-in failed")
                }
        }
    }

    fun onKakaoSignIn(
        context: Context,
    ) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            loginUseCase.invokeKakaoLogin(context)
                .onSuccess { isNewUser ->
                    localStorageRepository.saveUserProvider("KAKAO")
                    // isNewUser가 true이면이니 signup false는 home
                    val petId = localStorageRepository.getPetId()

                    if (isNewUser || petId == -1) {
                        _sideEffect.emit(LoginSideEffect.NavigateToSignUp)
                    } else {
                        _sideEffect.emit(LoginSideEffect.NavigateToHome)
                    }

                    _state.update { it.copy(isLoading = false) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false) }
                    _sideEffect.emit(LoginSideEffect.ShowSnackBar("로그인에 실패했습니다\n잠시 후에 다시 시도해주세요"))
                    Timber.e(e, "Kakao sign-in failed")
                }
        }
    }
}
