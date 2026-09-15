package com.paw.key.presentation.ui.mypage.route.petinfo.model

import android.net.Uri
import androidx.compose.runtime.Immutable
import com.paw.key.presentation.ui.mypage.model.PetInfoModel
import com.paw.key.presentation.ui.signup.model.PetInfoItemModel
import com.paw.key.presentation.ui.signup.state.Gender
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class PetProfileState(
    val name: String = "",
    val birthday: String = "",
    val gender: Gender = Gender.UNKNOWN,
    val isNeutered: Boolean = false,
    val breed: String = "",
    val breedId: Int = 0,
    val imageUrl: Uri? = null,
    val imageId: Int = 0,
    val cameraUri: Uri? = null,
    val age: String = "",
    val energyLevel: String = "",
    val socialLevel: String = "",
    val isLoading: Boolean = false,
    val petBreedList : ImmutableList<PetInfoItemModel> = persistentListOf(),
    val petInfo: PetInfoModel = PetInfoModel()
)

sealed interface PetProfileSideEffect {
    data class ShowSnackBar(val message: String) : PetProfileSideEffect
    data object NavigateUp : PetProfileSideEffect
}