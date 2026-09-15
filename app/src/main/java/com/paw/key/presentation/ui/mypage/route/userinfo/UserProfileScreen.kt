package com.paw.key.presentation.ui.mypage.route.userinfo

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paw.key.core.designsystem.component.PawkeyButton
import com.paw.key.core.designsystem.component.TopBar
import com.paw.key.core.designsystem.theme.PawKeyTheme
import com.paw.key.core.util.DateDataVisualTransformation
import com.paw.key.presentation.ui.mypage.route.userinfo.component.UserEditTextField
import com.paw.key.presentation.ui.mypage.route.userinfo.component.UserGenderButton
import com.paw.key.presentation.ui.mypage.route.userinfo.component.UserProfileItem
import com.paw.key.presentation.ui.mypage.route.userinfo.model.UserProfileSideEffect
import com.paw.key.presentation.ui.mypage.route.userinfo.viewmodel.UserProfileViewModel

@Composable
fun UserProfileRoute(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UserProfileViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is UserProfileSideEffect.ShowSnackBar -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is UserProfileSideEffect.NavigateUp -> {
                    navigateUp()
                }
                else -> Unit
            }
        }
    }

    UserProfileScreen(
        name = state.value.name,
        gender = state.value.gender,
        birth = state.value.birth,
        navigateUp = navigateUp,
        onNameChange = viewModel::onNameChange,
        onBirthChange = viewModel::onBirthChange,
        onGenderChange = viewModel::onGenderChange,
        onSaveClick = viewModel::updateUser,
        modifier = modifier
    )
}

@Composable
private fun UserProfileScreen(
    name: String,
    gender: String,
    birth: String,
    navigateUp: () -> Unit,
    onNameChange: (String) -> Unit,
    onBirthChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = PawKeyTheme.colors.white1),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TopBar(
            title = "내 정보 수정",
            onBackClick = navigateUp
        )

        Spacer(modifier = Modifier.height(4.dp))

        UserProfileItem(
            label = "닉네임",
            profileItem = {
                UserEditTextField(
                    value = name,
                    onValueChange = onNameChange,
                    placeholder = "닉네임을 입력해주세요",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true,
                    singleLine = true
                )
            }
        )

        UserProfileItem(
            label = "생년월일",
            profileItem = {
                UserEditTextField(
                    value = birth,
                    onValueChange = onBirthChange,
                    placeholder = "YYYY-MM-DD",
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = DateDataVisualTransformation(),
                    enabled = true,
                    singleLine = true
                )
            }
        )

        UserProfileItem(
            label = "성별",
            profileItem = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    UserGenderButton(
                        user = "남성",
                        isSelect = gender == "M",
                        onClick = { onGenderChange("M") },
                        modifier = Modifier.weight(1f)
                    )
                    UserGenderButton(
                        user = "여성",
                        isSelect = gender == "F",
                        onClick = { onGenderChange("F") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        )

        Spacer(modifier = Modifier.weight(1F))

        PawkeyButton(
            text = "저장하기",
            enabled = name.isNotBlank() && birth.isNotBlank() && gender.isNotBlank(),
            onClick = onSaveClick,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(34.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun UserProfileScreenPreview() {
    PawKeyTheme {
        UserProfileScreen(
            name = "김도기",
            gender = "F",
            birth = "2002-06-21",
            navigateUp = {},
            onNameChange = {},
            onBirthChange = {},
            onGenderChange = {},
            onSaveClick = {}
        )
    }
}