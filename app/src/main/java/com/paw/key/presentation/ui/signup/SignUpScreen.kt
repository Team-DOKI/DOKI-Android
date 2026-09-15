package com.paw.key.presentation.ui.signup

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.paw.key.core.designsystem.component.DokiButton
import com.paw.key.core.designsystem.component.LoadingScreen
import com.paw.key.core.designsystem.theme.PawKeyTheme
import com.paw.key.core.util.UiState
import com.paw.key.presentation.ui.signup.component.SignUpHeader
import com.paw.key.presentation.ui.signup.component.SignUpSubHeader
import com.paw.key.presentation.ui.signup.model.DongModel
import com.paw.key.presentation.ui.signup.model.GuModel
import com.paw.key.presentation.ui.signup.model.PetInfoItemModel
import com.paw.key.presentation.ui.signup.model.SignUpLocationInfo
import com.paw.key.presentation.ui.signup.model.SignUpMapInfo
import com.paw.key.presentation.ui.signup.model.SignUpPetInfo
import com.paw.key.presentation.ui.signup.model.SignUpUserInfo
import com.paw.key.presentation.ui.signup.state.Gender
import com.paw.key.presentation.ui.signup.state.SignUpSideEffect
import com.paw.key.presentation.ui.signup.state.SignUpState
import com.paw.key.presentation.ui.signup.state.SignUpStateType
import com.paw.key.presentation.ui.signup.viewmodel.SignUpViewModel
import timber.log.Timber

@Composable
fun SignUpRoute(
    navigateUp: () -> Unit,
    navigateToDBTI: (isSignUp: Boolean) -> Unit,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        Timber.e("촬영 결과: $success")
        if (success) {
            state.petInfo.cameraUri?.let { viewModel.onPetImageChanged(it) }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Timber.e("권한 런처 결과 수신: isGranted = $isGranted") // 👈 로그 확인!
        if (isGranted) {
            viewModel.createCameraUri()
        } else {
            Timber.e("카메라 권한이 거부되어 진행할 수 없습니다.")
        }
    }

    LaunchedEffect(state.petInfo.cameraUri) {
        Timber.e("LaunchedEffect 감지됨! 현재 cameraUri = ${state.petInfo.cameraUri}")
        state.petInfo.cameraUri?.let { uri ->
            Timber.e("카메라 앱 진짜 실행합니다: $uri")
            takePictureLauncher.launch(uri)
        }
    }

    BackHandler(enabled = true) {
        viewModel.onBackPressed()
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.flowWithLifecycle(lifecycleOwner.lifecycle)
            .collect { it ->
                when (it) {
                    is SignUpSideEffect.NavigateUp -> {
                        navigateUp()
                    }
                    is SignUpSideEffect.ShowSnackBar -> {

                    }
                    is SignUpSideEffect.NavigateNext -> {
                        viewModel.updateStep()
                    }
                    is SignUpSideEffect.NavigateDBTI -> {
                        navigateToDBTI(it.isSignUp)
                    }

                    is SignUpSideEffect.LaunchCamera -> {
                    }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = PawKeyTheme.colors.background)
    ) {
        SignUpScreen(
            state = state,
            currentStep = state.currentStep,
            type = state.signUpState,
            isNextEnabled = state.isNextEnabled,

            onNextClick = viewModel::onNextClick,
            onBackClick = viewModel::onBackPressed,

            userInfo = state.userInfo,
            onNickNameChanged = { viewModel.updateNickname(it) },
            onBirthDateChanged = { viewModel.updateBirthDate(it) },
            onGenderChanged = viewModel::updateGender,

            petInfo = state.petInfo,
            onPetNameChanged = { viewModel.updatePetName(it) },
            onPetBirthDateChanged = { viewModel.updatePetBirthDate(it) },
            onPetGenderChanged = viewModel::updatePetGender,
            onPetNeuteredChanged = viewModel::updatePetNeutered,
            onPetBreedChanged = { viewModel.updatePetBreed(it) },
            deniedPermission = viewModel::deniedPermission,
            onSelectedImage = viewModel::updatePetImage,
            requestPetInfo = viewModel::getPetInfo,
            onCameraClick = {
                val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED

                if (hasPermission) {
                    viewModel.createCameraUri()
                } else {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },


            locationInfo = state.locationInfo,
            getRegions = viewModel::getRegions,
            onRegionSelected = { gu, dong ->
                viewModel.updateLocation(gu, dong)
            },

            mapInfo = state.mapInfo,
        )

        if (state.isLoading) {
            LoadingScreen()
        }
    }
}

@Composable
fun SignUpScreen(
    state: SignUpState,
    userInfo : SignUpUserInfo,
    onNickNameChanged: (String) -> Unit,
    onBirthDateChanged: (String) -> Unit,
    onGenderChanged: (Gender) -> Unit,

    petInfo : SignUpPetInfo,
    deniedPermission: () -> Unit,
    onPetNameChanged : (String) -> Unit,
    onPetBirthDateChanged : (String) -> Unit,
    onPetGenderChanged : (Gender) -> Unit,
    onPetNeuteredChanged : (Boolean) -> Unit,
    onPetBreedChanged : (PetInfoItemModel) -> Unit,
    onSelectedImage: (Uri?) -> Unit,
    requestPetInfo : () -> Unit,
    onCameraClick: () -> Unit,

    locationInfo : SignUpLocationInfo,
    getRegions: () -> Unit,
    onRegionSelected: (GuModel, DongModel) -> Unit,

    mapInfo: SignUpMapInfo,

    isNextEnabled: Boolean,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit,
    currentStep : Float,
    type: SignUpStateType,
) {
    val title = when(type) {
        SignUpStateType.USER_INFO -> {
            "내 정보 입력"
        }
        SignUpStateType.PET_INFO -> {
            "반려견 정보 입력"
        }
        SignUpStateType.LOCATION_INFO -> {
            "산책 지역 입력"
        }
        SignUpStateType.REGION_MANAGEMENT -> {
            "산책 지역 입력"
        }
    }

    val buttonText = when(type) {
        SignUpStateType.LOCATION_INFO -> {
            "완료"
        }
        else -> {
            "다음"
        }
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(color = PawKeyTheme.colors.background)
    ) {
        SignUpHeader(
            title = title,
            onBackClick = onBackClick,
            progress = currentStep
        )

        when (type) {
            SignUpStateType.REGION_MANAGEMENT -> {
                when (val state = mapInfo.uiState) {
                    is UiState.Loading -> {
                        LoadingScreen()
                    }

                    is UiState.Success -> {
                        SignUpMapInfoScreen(
                            type = mapInfo.drawType,
                            regionCoordinates = state.data,
                            entireCoordinates = mapInfo.entireCoordinates,
                            regionName = mapInfo.regionName,
                            onClickButton = onNextClick,
                            modifier = Modifier
                        )
                    }

                    else -> {}
                }
            }

            else -> {
                SignUpSubHeader()

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(color = PawKeyTheme.colors.background)
                        .verticalScroll(rememberScrollState())
                ) {
                    when (type) {
                        SignUpStateType.USER_INFO -> {
                            SignUpUserInfoScreen(
                                nickName = userInfo.nickName,
                                birthDate = userInfo.birthDate,
                                gender = userInfo.gender,
                                isDuplicate = userInfo.isDuplicate,
                                onNickNameChanged = onNickNameChanged,
                                onBirthDateChanged = onBirthDateChanged,
                                onGenderChanged = onGenderChanged,
                                modifier = Modifier
                                    .padding(top = 40.dp)
                            )
                        }

                        SignUpStateType.PET_INFO -> {
                            SignUpPetInfoScreen(
                                petInfo = petInfo,
                                petBreedList = state.petBreedList,
                                onPetNameChanged = onPetNameChanged,
                                onPetBirthDateChanged = onPetBirthDateChanged,
                                onPetGenderChanged = onPetGenderChanged,
                                onPetNeuteredChanged = onPetNeuteredChanged,
                                onPetBreedChanged = onPetBreedChanged,
                                deniedPermission = deniedPermission,
                                onSelectedImage = {
                                    onSelectedImage(it)
                                },
                                requestPetInfo = requestPetInfo,
                                onCameraClick = {
                                    Timber.e("oncameraclick signupscreen")
                                    onCameraClick()
                                }
                            )
                        }

                        SignUpStateType.LOCATION_INFO -> {
                            SignUpLocationInfoScreen(
                                locationInfo = locationInfo,
                                getRegions = getRegions,
                                onRegionSelected = onRegionSelected,
                                modifier = Modifier
                            )
                        }

                        else -> {}
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    DokiButton(
                        text = buttonText,
                        onClick = onNextClick,
                        enabled = isNextEnabled,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 32.dp
                            )
                    )
                }
            }
        }
    }
}
