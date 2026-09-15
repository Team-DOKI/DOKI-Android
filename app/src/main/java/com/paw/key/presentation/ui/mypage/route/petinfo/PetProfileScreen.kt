package com.paw.key.presentation.ui.mypage.route.petinfo

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paw.key.R
import com.paw.key.core.designsystem.component.PawKeyBottomSheet
import com.paw.key.core.designsystem.component.PawkeyButton
import com.paw.key.core.designsystem.component.TopBar
import com.paw.key.core.designsystem.theme.PawKeyTheme
import com.paw.key.core.extension.collectSideEffect
import com.paw.key.core.extension.noRippleClickable
import com.paw.key.core.util.DateDataVisualTransformation
import com.paw.key.presentation.ui.mypage.route.petinfo.model.PetProfileSideEffect
import com.paw.key.presentation.ui.mypage.route.petinfo.viewmodel.PetProfileViewModel
import com.paw.key.presentation.ui.signup.component.FormField
import com.paw.key.presentation.ui.signup.component.GenderSelector
import com.paw.key.presentation.ui.signup.component.ImageTypeSelectDialog
import com.paw.key.presentation.ui.signup.component.PetBreedSearchContent
import com.paw.key.presentation.ui.signup.component.SignUpNeuteringCheckRadio
import com.paw.key.presentation.ui.signup.component.SignUpPetImageHolder
import com.paw.key.presentation.ui.signup.component.SignUpTextField
import com.paw.key.presentation.ui.signup.model.PetInfoItemModel
import com.paw.key.presentation.ui.signup.state.Gender
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun PetProfileRoute(
    navigateUp: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: PetProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            state.cameraUri?.let { viewModel.onImageChange(it) }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.createCameraUri()
        } else {
            Timber.e("카메라 권한이 거부되어 진행할 수 없습니다.")
        }
    }

    LaunchedEffect(state.cameraUri) {
        state.cameraUri?.let { uri ->
            takePictureLauncher.launch(uri)
        }
    }

    viewModel.sideEffect.collectSideEffect {
        when (it) {
            is PetProfileSideEffect.ShowSnackBar -> snackbarHostState.showSnackbar(it.message)
            PetProfileSideEffect.NavigateUp      ->  navigateUp()
        }
    }

    PetProfileScreen(
        petName            = state.name,
        petBirthDate       = state.birthday,
        petGender          = state.gender,
        petNeutered        = state.isNeutered,
        petBreed           = state.breed,
        selectedImageUri   = state.imageUrl,
        isLoading          = state.isLoading,
        petBreedList = state.petBreedList,
        navigateUp         = navigateUp,
        deniedPermission   = {},
        onPetNameChanged   = viewModel::onNameChange,
        onPetBirthDateChanged = viewModel::onBirthChange,
        onPetGenderChanged = viewModel::onGenderChange,
        onPetNeuteredChanged = viewModel::onNeuteredChange,
        onPetBreedChanged  = { viewModel.onBreedChange(it.name, it.id) },
        onSelectedImage    = viewModel::onImageChange,
        onCameraClick      = {
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
        onDefaultClick     = viewModel::setDefaultImage,
        onSaveClick        = viewModel::updatePet,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetProfileScreen(
    petName: String,
    petBirthDate: String,
    petGender: Gender,
    petNeutered: Boolean,
    petBreed: String,
    selectedImageUri: Uri?,
    isLoading: Boolean,
    petBreedList: ImmutableList<PetInfoItemModel>,
    navigateUp: () -> Unit,
    deniedPermission: () -> Unit,
    onPetNameChanged: (String) -> Unit,
    onPetBirthDateChanged: (String) -> Unit,
    onPetGenderChanged: (Gender) -> Unit,
    onPetNeuteredChanged: (Boolean) -> Unit,
    onPetBreedChanged: (PetInfoItemModel) -> Unit,
    onSelectedImage: (Uri?) -> Unit,
    onCameraClick: () -> Unit,
    onDefaultClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isSheetOpen by remember { mutableStateOf(false) }
    var isImageTypeDialogOpen by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val petBirthDateFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = onSelectedImage,
    )

    val legacyGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = onSelectedImage,
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) legacyGalleryLauncher.launch("image/*")
            else deniedPermission()
        },
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PawKeyTheme.colors.background),
    ) {
        TopBar(title = "반려견 정보 수정", onBackClick = navigateUp)

        HorizontalDivider(
            modifier  = Modifier.fillMaxWidth(),
            thickness = 2.dp,
            color     = PawKeyTheme.colors.defaultButton,
        )

        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                SignUpPetImageHolder(
                    uri = selectedImageUri,
                    modifier = Modifier.noRippleClickable {
                        isImageTypeDialogOpen = true
                    },
                )
            }

            item {
                FormField(
                    label = "이름",
                    content = {
                        SignUpTextField(
                            value         = petName,
                            onValueChange = { if (it.length <= 8) onPetNameChanged(it) },
                            placeholder   = "최대 8글자 이내로 입력해주세요",
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(
                                onNext = { petBirthDateFocusRequester.requestFocus() }
                            ),
                        )
                    },
                )
            }

            item {
                FormField(
                    label = "생년월일",
                    content = {
                        SignUpTextField(
                            modifier      = Modifier.focusRequester(petBirthDateFocusRequester),
                            value         = petBirthDate,
                            onValueChange = onPetBirthDateChanged,
                            visualTransformation = DateDataVisualTransformation(),
                            placeholder   = "YYYY-MM-DD",
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction    = ImeAction.Done,
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus() }
                            ),
                        )
                    },
                )
            }

            item {
                FormField(
                    label = "성별",
                    content = {
                        GenderSelector(
                            selectedGender   = petGender,
                            onGenderSelected = onPetGenderChanged,
                            type             = "반려 동물",
                        )
                    },
                )
            }

            item {
                SignUpNeuteringCheckRadio(
                    isNeutered = petNeutered,
                    onToggle   = { onPetNeuteredChanged(!petNeutered) },
                    modifier   = Modifier.padding(top = 8.dp),
                )
            }

            item {
                FormField(
                    label = "견종",
                    content = {
                        SignUpTextField(
                            value         = petBreed,
                            onValueChange = {},
                            enabled       = false,
                            placeholder   = "견종을 검색해보세요",
                            suffix = {
                                Icon(
                                    imageVector     = ImageVector.vectorResource(R.drawable.ic_signup_search),
                                    contentDescription = "breed search",
                                    tint            = Color.Unspecified,
                                )
                            },
                            modifier = Modifier.noRippleClickable {
                                scope.launch { isSheetOpen = true }
                            },
                        )
                    },
                )

                if (isSheetOpen) {
                    PawKeyBottomSheet(
                        onDismissRequest = { isSheetOpen = false },
                        sheetState       = sheetState,
                    ) { state ->
                        PetBreedSearchContent(
                            petBreedList  = petBreedList,
                            sheetState    = state,
                            selectedBreed = petBreed,
                            onBreedSelected = { breed ->
                                onPetBreedChanged(breed)
                                scope.launch {
                                    state.hide()
                                }.invokeOnCompletion {
                                    if (!state.isVisible) isSheetOpen = false
                                }
                            },
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        PawkeyButton(
            text     = "저장하기",
            enabled  = !isLoading,
            onClick  = onSaveClick,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(modifier = Modifier.height(34.dp))
    }

    if (isImageTypeDialogOpen) {
        ImageTypeSelectDialog(
            onCameraClick = {
                onCameraClick()
                isImageTypeDialogOpen = false
            },
            onGalleryClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                isImageTypeDialogOpen = false
            },
            onDefaultClick = {
                onDefaultClick()
                isImageTypeDialogOpen = false
            },
            onDismissRequest = { isImageTypeDialogOpen = false }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PetProfileScreenPreview() {
    PawKeyTheme {
        PetProfileScreen(
            petName              = "꾸꾸",
            petBirthDate         = "20220101",
            petGender            = Gender.MALE,
            petNeutered          = true,
            petBreed             = "말티즈",
            selectedImageUri     = null,
            petBreedList = persistentListOf(),
            isLoading            = false,
            navigateUp           = {},
            deniedPermission     = {},
            onPetNameChanged     = {},
            onPetBirthDateChanged = {},
            onPetGenderChanged   = {},
            onPetNeuteredChanged = {},
            onPetBreedChanged    = {},
            onSelectedImage      = {},
            onCameraClick        = {},
            onDefaultClick       = {},
            onSaveClick          = {},
        )
    }
}