package com.paw.key.presentation.ui.region

import android.view.Gravity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.PolygonOverlay
import com.naver.maps.map.compose.rememberCameraPositionState
import com.paw.key.core.designsystem.component.CustomSnackBar
import com.paw.key.core.designsystem.component.DokiBorderButton
import com.paw.key.core.designsystem.component.DokiButton
import com.paw.key.core.designsystem.component.LoadingScreen
import com.paw.key.core.designsystem.theme.PawKeyTheme
import com.paw.key.core.util.UiState
import com.paw.key.presentation.ui.region.model.RegionDongModel
import com.paw.key.presentation.ui.region.model.RegionGuModel
import com.paw.key.presentation.ui.region.model.RegionStep
import com.paw.key.presentation.ui.region.state.DrawType
import com.paw.key.presentation.ui.region.state.RegionSideEffect
import com.paw.key.presentation.ui.region.state.RegionState
import com.paw.key.presentation.ui.region.viewmodel.RegionViewModel
import com.paw.key.presentation.ui.signup.component.SignUpHeader
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import timber.log.Timber

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun RegionalManagementRoute(
    paddingValues: PaddingValues,
    snackBarHostState: SnackbarHostState,
    navigateUp: () -> Unit,
    navigateNext: () -> Unit,
    regionId: Int? = -1,
    viewModel: RegionViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPositionState = rememberCameraPositionState()

    val bottomPanelHeightPx = remember {
        mutableIntStateOf(0)
    }

    val density = LocalDensity.current

    LaunchedEffect(Unit) {
        viewModel.sideEffect.flowWithLifecycle(lifecycleOwner.lifecycle)
            .collect { sideEffect ->
                when (sideEffect) {
                    is RegionSideEffect.ShowSnackBar -> {
                        snackBarHostState.showSnackbar(
                            sideEffect.message
                        )
                    }

                    RegionSideEffect.NavigateNext -> {
                        Timber.e("region next")
                        navigateNext()
                    }
                    RegionSideEffect.NavigateUp -> navigateUp()
                }
            }
    }

    when (state.currentStep) {
        RegionStep.SEARCH -> {
            RegionalManagementScreen(
                paddingValues = paddingValues,
                snackBarHostState = snackBarHostState,
                cameraPositionState = cameraPositionState,
                regionCoordinates = persistentListOf(),
                type = state.drawType,
                state = state,
                onClickButton = { viewModel.patchRegion() },
                onModifyClick = { viewModel.onBackPressedToSearch() },
                onSizeChanged = { bottomPanelHeightPx.intValue = it },
                onBackClick = { viewModel.onBackPressed() },
                onRegionSelected = { gu, dong -> viewModel.onRegionSelected(gu, dong) },
                onSaveRegionClick = { viewModel.patchRegion() },
                modifier = Modifier
            )
        }

        RegionStep.MAP -> {
            when (val uiState = state.uiState) {
                is UiState.Success -> {
                    LaunchedEffect(state.entireCoordinates.size) {
                        if (state.entireCoordinates.size >= 2 && bottomPanelHeightPx.intValue > 0) {
                            val bounds = LatLngBounds.from(state.entireCoordinates)
                            val bottomPadding = bottomPanelHeightPx.intValue + with(density) { 100.dp.roundToPx() }

                            cameraPositionState.move(
                                CameraUpdate.fitBounds(bounds, 100, 100, 100, bottomPadding)
                            )
                        }
                    }

                    RegionalManagementScreen(
                        paddingValues = paddingValues,
                        snackBarHostState = snackBarHostState,
                        cameraPositionState = cameraPositionState,
                        regionCoordinates = uiState.data,
                        type = state.drawType,
                        state = state,
                        onClickButton = { viewModel.confirmRegionOnMap() },
                        onModifyClick = { viewModel.onBackPressedToSearch() },
                        onSizeChanged = { bottomPanelHeightPx.intValue = it },
                        onBackClick = { viewModel.onBackPressed() },
                        onRegionSelected = { gu, dong -> viewModel.onRegionSelected(gu, dong) },
                        onSaveRegionClick = { viewModel.patchRegion() },
                        modifier = Modifier
                    )
                }

                is UiState.Loading -> {
                    LoadingScreen()
                }

                else -> {
                    Timber.e("Failure")
                }
            }
        }
    }
}

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun RegionalManagementScreen(
    paddingValues: PaddingValues,
    snackBarHostState: SnackbarHostState,
    cameraPositionState: CameraPositionState,
    state: RegionState,
    type: DrawType,
    regionCoordinates: ImmutableList<ImmutableList<LatLng>>,
    onClickButton: () -> Unit,
    onModifyClick: () -> Unit,
    onSizeChanged: (Int) -> Unit,
    onRegionSelected: (RegionGuModel, RegionDongModel) -> Unit,
    onBackClick: () -> Unit,
    onSaveRegionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            SignUpHeader(
                title = "산책 지역 입력",
                onBackClick = onBackClick,
                progress = 0f
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier.padding(
                    bottom = LocalWindowInfo.current.containerSize.height.dp * 0.4f
                )
            ) { data ->
                CustomSnackBar(
                    data = data
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = PawKeyTheme.colors.background
                )
                .padding(paddingValues)
                .padding(innerPadding)
        ) {
            when (state.currentStep) {
                RegionStep.SEARCH -> {
                    RegionSearchScreen(
                        regionList = state.regionList.toImmutableList(),
                        selectedGu = state.selectedGu,
                        selectedDong = state.selectedDong,
                        onRegionSelected = { gu, dong ->
                            onRegionSelected(gu, dong)
                        },
                        onSaveRegionClick = onSaveRegionClick
                    )
                }

                RegionStep.MAP -> {
                    NaverMap(
                        modifier = Modifier
                            .align(Alignment.Center),
                        cameraPositionState = cameraPositionState,
                        uiSettings = MapUiSettings(
                            logoGravity = Gravity.TOP or Gravity.END,
                            isZoomControlEnabled = false,
                            isLogoClickEnabled = true
                        ),
                    ) {
                        when (type) {
                            DrawType.SINGLE -> {
                                val singlePolygonCoords = regionCoordinates.first()
                                if (singlePolygonCoords.isNotEmpty()) {
                                    PolygonOverlay(
                                        coords = singlePolygonCoords,
                                        color = PawKeyTheme.colors.opacity25Primary.copy(alpha = 0.3f),
                                        outlineWidth = 1.dp,
                                        outlineColor = PawKeyTheme.colors.green500
                                    )
                                }
                            }

                            DrawType.MULTIPLE -> {
                                regionCoordinates.forEach {
                                    PolygonOverlay(
                                        coords = it,
                                        color = PawKeyTheme.colors.opacity25Primary.copy(alpha = 0.3f),
                                        outlineWidth = 1.dp,
                                        outlineColor = PawKeyTheme.colors.green500
                                    )
                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            .background(
                                color = PawKeyTheme.colors.white1,
                                shape = RoundedCornerShape(
                                    topStart = 16.dp, topEnd = 16.dp
                                )
                            )
                            .padding(horizontal = 16.dp, vertical = 24.dp)
                            .onSizeChanged { size ->
                                onSizeChanged(size.height)
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "선택한 위치",
                                style = PawKeyTheme.typography.header3,
                                color = PawKeyTheme.colors.contents
                            )

                            Text(
                                text = state.regionName,
                                style = PawKeyTheme.typography.header3,
                                color = PawKeyTheme.colors.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "선택한 산책 지역은 ${state.regionName}이에요.\n이 위치로 산책 지역을 설정하시겠어요?",
                            style = PawKeyTheme.typography.bodyDefault,
                            color = PawKeyTheme.colors.gray500,
                            modifier = Modifier
                                .padding(bottom = 12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DokiBorderButton(
                                text = "위치 수정하기",
                                onClick = onModifyClick,
                                modifier = Modifier.weight(1f),
                                enabled = true,
                            )

                            DokiButton(
                                text = "선택",
                                onClick = onClickButton,
                                modifier = Modifier.weight(1f),
                                enabled = true,
                            )
                        }
                    }
                }
            }
        }
    }
}