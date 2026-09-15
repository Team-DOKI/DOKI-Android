package com.paw.key.presentation.ui.splash.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paw.key.data.network.TokenRefreshService
import com.paw.key.domain.repository.localstorage.LocalStorageRepository
import com.paw.key.presentation.ui.splash.state.SplashSideEffect
import com.paw.key.presentation.ui.splash.state.SplashState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val localStorageRepository: LocalStorageRepository,
    private val reIssueManager: TokenRefreshService,
) : ViewModel() {

    private val _state = MutableStateFlow(SplashState())
    val state: StateFlow<SplashState>
        get() = _state.asStateFlow()

    private val _sideEffect = Channel<SplashSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()


    init {
        checkToken()
    }

    private fun checkToken() {
        Timber.e("checkToken")
        viewModelScope.launch {
            val refreshToken = localStorageRepository.getRefreshToken()

            if (refreshToken.isEmpty()) {
                _sideEffect.send(SplashSideEffect.NavigateToLogin)
                return@launch
            }

            val deviceId = localStorageRepository.getDeviceId()
            reIssueManager.refresh(refreshToken, deviceId)
                .onSuccess { (accessToken, newRefreshToken) ->
                    Timber.e("checkTokenSuc $accessToken")
                    localStorageRepository.saveTokens(accessToken.value, newRefreshToken.value)
                    val petId = localStorageRepository.getPetId()

                    if (petId == -1) {
                        // 기존 회원이지만 petId가 없는 경우 (회원가입/펫등록 프로세스 미완료)
                        _sideEffect.send(SplashSideEffect.NavigateToSignUp)
                    } else {
                        // 정상적으로 로그인 + petId까지 있는 경우
                        _sideEffect.send(SplashSideEffect.NavigateToHome)
                    }
                }
                .onFailure {
                    Timber.e("checkTokenFail $it")
                    localStorageRepository.clearInfo()
                    _sideEffect.send(SplashSideEffect.NavigateToLogin)
                }
        }
    }
}
