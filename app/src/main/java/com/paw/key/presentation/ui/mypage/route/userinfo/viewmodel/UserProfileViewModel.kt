package com.paw.key.presentation.ui.mypage.route.userinfo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paw.key.domain.repository.mypage.MypageRepository
import com.paw.key.domain.repository.user.UserRepository
import com.paw.key.presentation.ui.mypage.main.model.NON_DIGIT_REGEX
import com.paw.key.presentation.ui.mypage.route.userinfo.model.UserProfileSideEffect
import com.paw.key.presentation.ui.mypage.route.userinfo.model.UserProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(

    private val userRepository: UserRepository,
    private val mypageRepository: MypageRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(UserProfileState())
    val state: StateFlow<UserProfileState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<UserProfileSideEffect>()
    val sideEffect: MutableSharedFlow<UserProfileSideEffect> = _sideEffect

    init {
        getUserProfiles()
    }

    fun onNameChange(value: String) = _state.update { it.copy(name = value) }

    fun onBirthChange(value: String) {
        val digitsOnly = value.replace(NON_DIGIT_REGEX, "").take(8)

        _state.update { it.copy(birth = digitsOnly) }
    }

    fun onGenderChange(value: String) = _state.update { it.copy(gender = value) }
    fun getUserProfiles() {
        viewModelScope.launch {
            userRepository.getUserProfiles()
                .onSuccess { result ->
                    _state.update { state ->
                        state.copy(
                            name = result.name,
                            gender = if (result.gender == "남성") "M" else "F",
                            birth = result.birth?.replace(NON_DIGIT_REGEX, "").orEmpty(),
//                            email = result.email
                        )
                    }
                }
                .onFailure { e ->
                    Timber.e(e)
                    _sideEffect.emit(UserProfileSideEffect.ShowSnackBar(e.message ?: "프로필을 불러오지 못했습니다"))
                }
        }
    }

    fun updateUser() {
        if (_state.value.isLoading) return
        val s = _state.value

        if (s.name.isBlank() || s.birth.isBlank() || s.gender.isBlank()) {
            viewModelScope.launch {
                _sideEffect.emit(UserProfileSideEffect.ShowSnackBar("모든 정보를 입력해주세요."))
            }
            return
        }

        val formattedBirth = if (s.birth.length == 8) {
            "${s.birth.substring(0, 4)}-${s.birth.substring(4, 6)}-${s.birth.substring(6)}"
        } else {
            s.birth
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            mypageRepository.updateUser(
                name = s.name,
                birth = formattedBirth,
                gender = s.gender
            ).onSuccess {
                _sideEffect.emit(UserProfileSideEffect.ShowSnackBar("프로필이 수정되었습니다"))
                _sideEffect.emit(UserProfileSideEffect.NavigateUp)
                _state.update { it.copy(isLoading = false) }
            }.onFailure { e ->
                Timber.e(e)
                _sideEffect.emit(UserProfileSideEffect.ShowSnackBar(e.message ?: "수정에 실패했습니다"))
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}
