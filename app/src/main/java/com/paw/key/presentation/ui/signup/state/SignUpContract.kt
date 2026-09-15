package com.paw.key.presentation.ui.signup.state

import androidx.compose.runtime.Immutable
import com.paw.key.presentation.ui.signup.model.PetInfoItemModel
import com.paw.key.presentation.ui.signup.model.SignUpLocationInfo
import com.paw.key.presentation.ui.signup.model.SignUpMapInfo
import com.paw.key.presentation.ui.signup.model.SignUpPetInfo
import com.paw.key.presentation.ui.signup.model.SignUpUserInfo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class SignUpState(
    val userInfo: SignUpUserInfo = SignUpUserInfo(),
    val petInfo: SignUpPetInfo = SignUpPetInfo(),
    val petBreedList : ImmutableList<PetInfoItemModel> = persistentListOf(),
    val locationInfo: SignUpLocationInfo = SignUpLocationInfo(),
    val mapInfo: SignUpMapInfo = SignUpMapInfo(),
    val signUpState: SignUpStateType = SignUpStateType.USER_INFO,
    val currentStep: Float = 1f,
    val isNextEnabled: Boolean = false,
    val isRegionComplete: Boolean = false,
    val isLoading: Boolean = false,
)

sealed interface SignUpSideEffect {
    data class ShowSnackBar(val message: String) : SignUpSideEffect
    data object NavigateUp : SignUpSideEffect
    data object NavigateNext : SignUpSideEffect
    data class NavigateDBTI(val isSignUp: Boolean = true) : SignUpSideEffect
    data class LaunchCamera(val uriString: String) : SignUpSideEffect
}

enum class SignUpStateType {
    USER_INFO,
    PET_INFO,
    LOCATION_INFO,
    REGION_MANAGEMENT,
}

enum class Gender(
    val value: String
) {
    MALE("M"),
    FEMALE("F"),
    UNKNOWN("U")
}
