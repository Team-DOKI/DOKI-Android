package com.paw.key.presentation.ui.mypage.main

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paw.key.core.designsystem.component.TopBar
import com.paw.key.core.designsystem.component.dialog.DokiDialog
import com.paw.key.core.designsystem.theme.PawKeyTheme
import com.paw.key.core.extension.collectSideEffect
import com.paw.key.presentation.ui.mypage.main.component.MyList
import com.paw.key.presentation.ui.mypage.main.component.MyPageCard
import com.paw.key.presentation.ui.mypage.main.component.OwnerCard
import com.paw.key.presentation.ui.mypage.main.component.SettingList
import com.paw.key.presentation.ui.mypage.main.component.WithdrawalReasonDialog
import com.paw.key.presentation.ui.mypage.main.model.MyListState
import com.paw.key.presentation.ui.mypage.main.model.MyPageSideEffect
import com.paw.key.presentation.ui.mypage.main.model.MyPageState
import com.paw.key.presentation.ui.mypage.main.viewmodel.MyPageViewModel
import com.paw.key.presentation.ui.mypage.route.courseinfo.model.CourseType
@Composable
fun MyPageRoute(
    paddingValues: PaddingValues,
    navigateUp: () -> Unit,
    navigatePetProfile: () -> Unit,
    navigateCourseInfo: (CourseType) -> Unit,
    navigatePetProfileList: () -> Unit,
    navigateUserProfile: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToRegionSetting: () -> Unit,
    navigateDbtiStart: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyPageViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    var isShowLogOutDialog by remember { mutableStateOf(false) }
    var isShowWithDrawDialog by remember { mutableStateOf(false) }
    var isShowDeleteDialog by remember { mutableStateOf(false) }

    viewModel.sideEffect.collectSideEffect {
        when(it) {
            MyPageSideEffect.NavigateNext -> TODO()
            MyPageSideEffect.NavigateToLogin -> navigateToLogin()
            MyPageSideEffect.NavigateUp -> TODO()
            else -> {
                Toast.makeText(context, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getUserProfiles()
        viewModel.getPetProfiles()
    }

    MyPageScreen(
        state = state,
        paddingValues = paddingValues,
        deleteUser = viewModel::removeUser,
        navigateUp = navigateUp,
        navigatePetProfile = navigatePetProfile,
        navigateCourseInfo = navigateCourseInfo,
        navigatePetProfileList = navigatePetProfileList,
        navigateUserProfile = navigateUserProfile,
        onLogOutClick = { isShowLogOutDialog = true },
        onWithDrawClick = { isShowWithDrawDialog = true },
        onUpdateRegion = navigateToRegionSetting,
        navigateDbtiStart = navigateDbtiStart,
        modifier = modifier
    )

    if (isShowLogOutDialog) {
        DokiDialog(
            onDismiss = { isShowLogOutDialog = false },
            onConfirm = viewModel::logOutUser,
            title = "로그아웃",
            subDescription = "진짜로 로그아웃 하시게요?",
            confirmText = "로그아웃",
            dismissText = "취소"
        )
    }

    if (isShowDeleteDialog) {
        DokiDialog(
            onDismiss = { isShowDeleteDialog = false },
            onConfirm = viewModel::removeUser,
            title = "탈퇴하기",
            subDescription = "진짜로 탈퇴하시게요?",
            confirmText = "탈퇴하기",
            dismissText = "취소"
        )
    }

    if (isShowWithDrawDialog) {
        WithdrawalReasonDialog(
            onDismiss = { isShowWithDrawDialog = false },
            onNextStep = {
                isShowWithDrawDialog = false
                isShowDeleteDialog = true
            },
            onKeepUsing = {
                isShowWithDrawDialog = false
            }
        )
    }
}

@Composable
fun MyPageScreen(
    state: MyPageState,
    paddingValues: PaddingValues,
    deleteUser: () -> Unit,
    navigateUp: () -> Unit,
    navigatePetProfile: () -> Unit,
    navigateCourseInfo: (CourseType) -> Unit,
    navigatePetProfileList: () -> Unit,
    navigateUserProfile: () -> Unit,
    onLogOutClick: () -> Unit,
    onWithDrawClick: () -> Unit,
    onUpdateRegion: () -> Unit,
    navigateDbtiStart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PawKeyTheme.colors.white2)
            .padding(paddingValues)
    ) {
        TopBar(
            title = "마이페이지",
            onBackClick = navigateUp,
            isBackVisible = false,
            onClickTitle = {}
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(PawKeyTheme.colors.defaultButton)
                .padding(horizontal = 16.dp)
                .padding(top = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OwnerCard(
                    ownerName = state.ownerName,
                    ownerEmail = state.ownerEmail,
                    navigateUserProfile = navigateUserProfile
                )
            }

            item {
                with(state.petInfo) {
                    MyPageCard(
                        userName = petName,
                        userAge = petAge,
                        userGender = petGender,
                        dogBreed = petBreed,
                        buttonTitle = if (petDbtiName.isEmpty()) "DBTI 검사하러 가기"
                                    else "${state.dbtiType} | $petDbtiName",
                        dogImage = petImageUrl,
                        onButtonClick = navigateDbtiStart,
                        navigatePetProfile = navigatePetProfile
                    )
                }
            }

            item {
                MyList(
                    listTitle = "산책 루트 관리",
                    listContent = MyListState(),
                    onListClick = { index ->
                        val courseType = when (index) {
                            0 -> CourseType.MyCourse
                            1 -> CourseType.AllCourse
                            2 -> CourseType.ReviewCourse
                            else -> CourseType.MyCourse
                        }
                        navigateCourseInfo(courseType)
                    }
                )
            }

            item {
                SettingList(
                    listTitle = "설정",
                    listContent = MyListState(),
                    onListClick = {
                        when(it) {
                            0 -> onUpdateRegion() // 산책 지역 설정
                            2 -> onLogOutClick() // 로그아웃
                            3 -> onWithDrawClick() // 탈퇴하기
                        }
                    }
                )

                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyPageScreenPreview() {
    PawKeyTheme {
        MyPageScreen(
            state = MyPageState(
                ownerName = "키큰오팔전차님",
                petTags = listOf("조금 느긋해요", "#오토바이소리", "#대형견"),
                walkCount = 7,
                totalDistance = "14km"
            ),
            paddingValues = PaddingValues(),
            navigateUp = {},
            navigatePetProfile = {},
            navigateCourseInfo = {},
            navigatePetProfileList = {},
            navigateUserProfile = {},
            deleteUser = {},
            onLogOutClick = {},
            onWithDrawClick = {},
            onUpdateRegion = {},
            navigateDbtiStart = {},
        )
    }
}