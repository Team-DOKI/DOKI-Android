package com.paw.key.presentation.ui.mypage.main.model

import androidx.compose.runtime.Immutable
import com.paw.key.presentation.ui.mypage.model.PetInfoModel

@Immutable
data class MyPageState(
    val ownerName: String = "님",
    val ownerEmail: String = "",

    val petInfo : PetInfoModel = PetInfoModel(),
    val petTags: List<String> = emptyList(),

    val dbtiType : String = "",
    val walkCount: Int = 0,
    val totalDistance: String = "",
)

sealed interface MyPageSideEffect {
    data class ShowSnackBar(val message: String) : MyPageSideEffect
    data object NavigateUp : MyPageSideEffect
    data object NavigateNext : MyPageSideEffect
    data object NavigateToLogin : MyPageSideEffect
}
