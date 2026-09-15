package com.paw.key.presentation.ui.mypage.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paw.key.core.app.AppRestarter
import com.paw.key.domain.repository.DBTI.DbtiRepository
import com.paw.key.domain.repository.localstorage.LocalStorageRepository
import com.paw.key.domain.repository.user.UserRepository
import com.paw.key.presentation.ui.mypage.main.model.MyPageSideEffect
import com.paw.key.presentation.ui.mypage.main.model.MyPageState
import com.paw.key.presentation.ui.mypage.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val localRepository: LocalStorageRepository,
    private val dbtiRepository: DbtiRepository,
    private val appRestarter: AppRestarter
) : ViewModel() {
    private val _state = MutableStateFlow(MyPageState())
    val state: StateFlow<MyPageState>
        get() = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<MyPageSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()


    fun getUserProfiles() {
        Timber.e("getUserProfiles")
        viewModelScope.launch {
            userRepository.getUserProfiles()
                .onSuccess { user ->
                    Timber.e("getUserProfiles: $user")
                    _state.update { state ->
                        state.copy(
                            ownerName = "${user.name}님",
                            ownerEmail = user.email
                        )
                    }
                }.onFailure { e ->
                    Timber.e("getUserProfiles: $e")
                    _sideEffect.emit(MyPageSideEffect.ShowSnackBar("유저 프로필 불러오기 실패"))
                }
        }
    }

    fun getPetProfiles() {
        viewModelScope.launch {
            val petId = localRepository.getPetId()

            launch {
                userRepository.getPetProfiles(petId)
                    .onSuccess { result ->
                        _state.update { currentState ->
                            currentState.copy(
                                petInfo = result.toUiModel()
                            )
                        }
                        Timber.e("getPetProfiles: $result")
                        Timber.e("getPetProfiles: ${_state.value.petInfo}")
                    }.onFailure {
                        Timber.e("getPetProfiles: $it")
                        _sideEffect.emit(MyPageSideEffect.ShowSnackBar("펫 프로필 불러오기 실패"))
                    }
            }

            launch {
                dbtiRepository.getResult(petId = petId.toLong())
                    .onSuccess { result ->
                        _state.update { currentState ->
                            currentState.copy(
                                dbtiType = result.type,
                                petInfo = currentState.petInfo.copy(
                                    petDbtiName = result.name,
                                    petDbtiDescription = result.description
                                )
                            )
                        }
                        Timber.e("getPetProfiles: $result")
                    }
                    .onFailure {
                        Timber.e("getPetProfiles: $it")
                    }
            }
        }
    }

    fun logOutUser() {
        viewModelScope.launch {
            val deviceId = localRepository.getDeviceId()

            userRepository.logOutUser(deviceId = deviceId)
                .onSuccess {
                    Timber.e("로그아웃 성공")
                    localRepository.clearInfo()
                    appRestarter.restartApp()
                }
                .onFailure {
                    Timber.e(it)
                    _sideEffect.emit(MyPageSideEffect.ShowSnackBar("로그아웃에 실패했어요 다시 시도해주세요"))
                }
        }
    }

    fun removeUser() {
        viewModelScope.launch {
            val userProvider = localRepository.getUserProvider()

            Timber.e("userProvider: $userProvider")

            userRepository.deleteUser(userProvider)
                .onSuccess {
                    Timber.e("유저 삭제 성공")
                    localRepository.clearInfo()
                    appRestarter.restartApp()
                }
                .onFailure {
                    Timber.e(it)
                    _sideEffect.emit(MyPageSideEffect.ShowSnackBar("유저 삭제 실패"))
                }
        }
    }
}
