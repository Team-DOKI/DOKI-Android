package com.paw.key.presentation.ui.mypage.route.petinfo.viewmodel

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paw.key.core.util.file.ImageUriManager
import com.paw.key.domain.repository.localstorage.LocalStorageRepository
import com.paw.key.domain.repository.mypage.MypageRepository
import com.paw.key.domain.repository.user.UserRepository
import com.paw.key.presentation.ui.mypage.main.model.NON_DIGIT_REGEX
import com.paw.key.presentation.ui.mypage.route.petinfo.model.PetProfileSideEffect
import com.paw.key.presentation.ui.mypage.route.petinfo.model.PetProfileState
import com.paw.key.presentation.ui.signup.model.toState
import com.paw.key.presentation.ui.signup.state.Gender
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
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
class PetProfileViewModel @Inject constructor(
    private val mypageRepository: MypageRepository,
    private val localRepository: LocalStorageRepository,
    private val userRepository: UserRepository,
    private val imageUriManager: ImageUriManager
) : ViewModel() {

    private val _state = MutableStateFlow(PetProfileState())
    val state: StateFlow<PetProfileState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<PetProfileSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    init {
        getPetProfiles()
    }

    fun onNameChange(value: String) = _state.update { it.copy(name = value) }
    fun onBirthChange(value: String) {
        val digitsOnly = value.replace(NON_DIGIT_REGEX, "").take(8)

        _state.update { it.copy(birthday = digitsOnly) }
    }

    fun onGenderChange(value: Gender) = _state.update { it.copy(gender = value) }
    fun onNeuteredChange(value: Boolean) = _state.update { it.copy(isNeutered = value) }
    fun onBreedChange(breedName: String, breedId: Int) =
        _state.update { it.copy(breed = breedName, breedId = breedId) }

    fun onImageChange(uri: Uri?) = _state.update { it.copy(imageUrl = uri) }

    fun setDefaultImage() = _state.update {
        it.copy(imageUrl = null, imageId = 0, cameraUri = null)
    }
    fun createCameraUri() {
        viewModelScope.launch {
            val uriString = imageUriManager.createTempImageUri() ?: return@launch
            val uri = uriString.toUri()
            _state.update { it.copy(cameraUri = uri) }
        }
    }
    fun getPetProfiles() {
        viewModelScope.launch {
            val petId = localRepository.getPetId()

            userRepository.getPetProfiles(petId)
                .onSuccess { result ->
                    _state.update { currentState ->
                        currentState.copy(
                            name = result.name,
                            birthday = result.birth.replace(NON_DIGIT_REGEX, ""),
                            gender = if (result.gender == "M") Gender.MALE else Gender.FEMALE,
                            isNeutered = result.isNeutered,
                            breed = result.breed,
                            imageUrl = result.imageUrl?.toUri(),
                        )
                    }

                    fetchPetBreedInfo(result.breed)
                }.onFailure {
                    _sideEffect.emit(PetProfileSideEffect.ShowSnackBar("펫 프로필 불러오기 실패"))
                }
        }
    }

    fun updatePet() {
        if (_state.value.isLoading) return
        val s = _state.value

        if (s.name.isBlank() || s.birthday.isBlank() || s.breedId == 0) {
            viewModelScope.launch {
                Timber.e("마이페이지 - 펫 프로필 수정 ${s}")
                _sideEffect.emit(PetProfileSideEffect.ShowSnackBar("필수 정보를 모두 입력해주세요"))
            }
            return
        }

        val formattedBirth = if (s.birthday.length == 8 && !s.birthday.contains("-")) {
            "${s.birthday.substring(0, 4)}-${s.birthday.substring(4, 6)}-${
                s.birthday.substring(
                    6,
                    8
                )
            }"
        } else {
            s.birthday.replace(".", "-").replace("/", "-")
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val petId = localRepository.getPetId()

            mypageRepository.updatePet(
                petId = petId,
                name = s.name,
                birth = formattedBirth,
                gender = if (s.gender == Gender.MALE) "M" else "F",
                isNeutered = s.isNeutered,
                breedId = s.breedId,
                imageId = s.imageId
            )
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _sideEffect.emit(PetProfileSideEffect.NavigateUp)
                }
                .onFailure { e ->
                    Timber.e(e)
                    _state.update { it.copy(isLoading = false) }
                    _sideEffect.emit(PetProfileSideEffect.ShowSnackBar(e.message ?: "수정에 실패했습니다"))
                }
        }
    }

    fun fetchPetBreedInfo(petBreed: String) {
        viewModelScope.launch {
            userRepository.getPetBreeds()
                .onSuccess { result ->
                    _state.update {
                        it.copy(
                            breedId = result.breedList.find { it.name == petBreed }?.id ?: 0,
                            petBreedList = result.breedList.map { it.toState() }.toImmutableList()
                        )
                    }

                    Timber.e("fetchPetBreedInfo : ${_state.value}")
                }
                .onFailure(Timber::e)
        }
    }
}