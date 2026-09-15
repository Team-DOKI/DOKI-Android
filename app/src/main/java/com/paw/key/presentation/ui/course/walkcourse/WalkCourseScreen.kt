package com.paw.key.presentation.ui.course.walkcourse

import android.Manifest
import android.os.Build
import android.view.Gravity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.keepScreenOn
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.ArrowheadPathOverlay
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.CameraUpdateReason
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationOverlay
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapEffect
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.PathOverlay
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.overlay.OverlayImage
import com.paw.key.R
import com.paw.key.core.designsystem.component.DokiBorderButton
import com.paw.key.core.designsystem.component.DokiButton
import com.paw.key.core.designsystem.component.LoadingScreen
import com.paw.key.core.designsystem.theme.PawKeyTheme
import com.paw.key.core.extension.noRippleClickable
import com.paw.key.core.util.PermissionRequestEffect
import com.paw.key.core.util.UiState
import com.paw.key.core.util.saveBitmapToCache
import com.paw.key.presentation.ui.course.util.FusedLocationSource
import com.paw.key.presentation.ui.course.util.StepCountListener
import com.paw.key.presentation.ui.course.util.rememberCustomFusedLocationSource
import com.paw.key.presentation.ui.course.util.rememberStepCounter
import com.paw.key.presentation.ui.course.walkcourse.component.WalkRecordItem
import com.paw.key.presentation.ui.course.walkcourse.state.WalkCourseSideEffect
import com.paw.key.presentation.ui.course.walkcourse.state.WalkCourseState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.drop
import timber.log.Timber
import java.util.Locale

private val REQUIRED_PERMISSIONS = mutableListOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
).apply {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        add(Manifest.permission.ACTIVITY_RECOGNITION)
    }
}.toTypedArray()

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun WalkCourseRoute(
    paddingValues: PaddingValues,
    navigateUp: () -> Unit = {},
    navigateSharedReview: (routeId: Int, isShared: Boolean, postId: Int, userId: Int) -> Unit = {_, _,_,_ -> },
    navigateWalkComplete: (routeId: Int, routeImageId: Int) -> Unit = {_, _ ->},
    viewModel: WalkCourseViewModel = hiltViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val state by viewModel.state.collectAsStateWithLifecycle()

    val cameraPositionState = rememberCameraPositionState()

    var hasLocationPermission by remember { mutableStateOf(false) }

    val fusedLocationClient = rememberCustomFusedLocationSource(
        useTestPoints = false,
        cameraPositionState = cameraPositionState,
        hasLocationPermission = hasLocationPermission
    )

    val stepCounter = rememberStepCounter()


    var mapProperties by remember {
        mutableStateOf(MapProperties())
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.flowWithLifecycle(lifecycleOwner.lifecycle)
            .collect { sideEffect ->
                when (sideEffect) {
                    is WalkCourseSideEffect.NavigateNext -> {}

                    WalkCourseSideEffect.NavigateUp -> navigateUp()

                    is WalkCourseSideEffect.ShowToastMessage -> {
                        Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
                    }

                    is WalkCourseSideEffect.NavigateSharedReview -> navigateSharedReview(sideEffect.routeId, sideEffect.isShared, sideEffect.postId, sideEffect.userId)

                    is WalkCourseSideEffect.NavigateComplete -> navigateWalkComplete(sideEffect.routeId, sideEffect.routeImageId ?: -1)

                    else -> {}
                }
            }
    }

    PermissionRequestEffect(
        permissions = REQUIRED_PERMISSIONS,
        onResult = { isGranted ->
            hasLocationPermission = isGranted
            if (isGranted) {
                viewModel.onPermissionsGranted()
                fusedLocationClient.setRealTimeLocationListener(viewModel)
            } else {
                viewModel.showToastMessage("산책 기록을 위해 권한이 필요합니다.")
            }
        }
    )

    LaunchedEffect(state.recordingState.isRecording, stepCounter) {
        if (state.recordingState.isRecording) {
            stepCounter.setStepCountListener(object : StepCountListener {
                override fun onStepCountChanged(sessionSteps: Long) {
                    viewModel.onRawStepData(sessionSteps)
                }
                override fun onSensorNotFound() {
                    Toast.makeText(context, "걸음 수 측정 센서가 없는 기기입니다.", Toast.LENGTH_SHORT).show()
                }
            })
            stepCounter.activate()
        } else {
            stepCounter.deactivate()
        }
    }

    LaunchedEffect(state.mapState.poiPoints.size, state.isShared) {
        if (state.isShared && state.mapState.poiPoints.size >= 2) {
            Timber.e("course isShared")
            val bounds = LatLngBounds.from(state.mapState.poiPoints)
            cameraPositionState.animate(
                update = CameraUpdate.fitBounds(bounds, 250),
                durationMs = 1000
            )
        }

        else if (!state.isShared && state.mapState.poiPoints.size >= 2) {
            Timber.e("course not isShared")
            val bounds = LatLngBounds.from(state.mapState.poiPoints)
            cameraPositionState.animate(
                update = CameraUpdate.fitBounds(bounds, 250)
            )
        }
    }

    LaunchedEffect(state.mapState.isTrackingEnabled) {
        mapProperties = mapProperties.copy(
            locationTrackingMode = if (state.mapState.isTrackingEnabled) {
                LocationTrackingMode.Follow
            } else {
                LocationTrackingMode.NoFollow
            }
        )
    }

    LaunchedEffect(cameraPositionState) {
        snapshotFlow { cameraPositionState.cameraUpdateReason }
            .drop(1)
            .collect { reason ->
                if (reason == CameraUpdateReason.GESTURE && state.mapState.isTrackingEnabled) {
                    viewModel.disableTracking()
                }
            }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            // ON_DESTROY: 화면을 뒤로가기로 벗어나거나 앱 프로세스가 종료될 때
            if (event == Lifecycle.Event.ON_DESTROY) {
                if (state.recordingState.isRecording && !state.isStopTracking) {
                    viewModel.stopTracking(snapshotUri = null)
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    when (state.mapState.initialState) {
        is UiState.Empty -> Unit
        is UiState.Failure -> Unit

        is UiState.Loading -> {
            LoadingScreen()
        }

        is UiState.Success -> {
            WalkCourseScreen(
                paddingValues = paddingValues,
                state = state,
                cameraPositionState = cameraPositionState,
                currentLocation = state.mapState.currentLocation,
                routeLineCoords = state.mapState.poiPoints,
                locationSource = fusedLocationClient,
                mapProperties = mapProperties,
                isRecording = state.recordingState.isRecording, // 산책 중단, 계속 여부
                isTracking = state.mapState.isTrackingEnabled, // 산책 포커싱
                onClickTracking = {
                    viewModel.fetchTrackingEnable()
                },
                onPauseTracking = { // 일시정지
                    viewModel.pauseTracking()
                },
                onStartTracking = { // 계속하기
                    viewModel.startTracking()
                },
                onStopTracking = {
                    viewModel.onStopTrackingRequested()
                },
                onConfirmStop = { uri ->
                    viewModel.stopTracking(snapshotUri = uri)
                },
                onCancelStop = {
                    viewModel.onStopTrackingCancelled()
                }
            )
        }
    }
}

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun WalkCourseScreen(
    paddingValues: PaddingValues,
    state: WalkCourseState,
    cameraPositionState: CameraPositionState,
    locationSource: FusedLocationSource,
    currentLocation : LatLng?,
    routeLineCoords : ImmutableList<LatLng>,
    mapProperties: MapProperties,
    isTracking: Boolean, // 포커싱 여부
    isRecording: Boolean, // 산책 중단, 계속 여부
    onClickTracking: () -> Unit, // 따라다니기
    onPauseTracking: () -> Unit, // 잠시 중단
    onStopTracking: () -> Unit,              // 산책 중단하기/종료하기 1차 클릭
    onConfirmStop: (snapshotUri: String?) -> Unit,  // 종료 확인 "예"
    onCancelStop: () -> Unit,                // 종료 취소 "아니오"
    onStartTracking: () -> Unit,             // 일시정지 상태에서 "이어서 하기"
) {
    val context = LocalContext.current
    var naverMapInstance by remember { mutableStateOf<com.naver.maps.map.NaverMap?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .then(
                if (isRecording && !state.isStopTracking) Modifier.keepScreenOn() else Modifier
            )
    ) {
        NaverMap(
            modifier = Modifier
                .fillMaxSize(),
            cameraPositionState = cameraPositionState,
            locationSource = locationSource,
            locale = Locale.KOREA,
            uiSettings = MapUiSettings(
                logoGravity = Gravity.TOP or Gravity.END,
                isZoomControlEnabled = false,
                isLogoClickEnabled = true
            ),
            properties = mapProperties,
        ) {
            MapEffect { map ->
                naverMapInstance = map
            }

            if (currentLocation != null) {
                LocationOverlay(
                    position = currentLocation,
                    icon = OverlayImage.fromResource(R.drawable.user_poi),
                    iconWidth = 72,
                    iconHeight = 72,
                )
            }

            if (routeLineCoords.isNotEmpty() && routeLineCoords.size >= 2) {
                PathOverlay(
                    coords = routeLineCoords,
                    width = 5.dp,
                    color = PawKeyTheme.colors.primary,
                    outlineWidth = 0.dp
                )
            }
            ArrowheadPathOverlay()
        }

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    FloatingActionButton(
                        shape = CircleShape,
                        onClick = onClickTracking,
                        containerColor = Color.White,
                        modifier = Modifier
                            .border(
                                width = 2.dp,
                                color = if (isTracking) PawKeyTheme.colors.primary else Color.Transparent,
                                shape = CircleShape
                            ),
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_course_map_tap_location_on),
                            contentDescription = "내 위치",
                            tint = if (isTracking) PawKeyTheme.colors.primary else Color.Unspecified
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    color = PawKeyTheme.colors.background,
                    shadowElevation = 10.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(Alignment.Bottom)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 32.dp, start = 16.dp, end = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            WalkRecordItem(
                                recordTitle = R.string.course_record_distance,
                                recordContent = state.formattedDistance
                            )
                            WalkRecordItem(
                                recordTitle = R.string.course_record_time,
                                recordContent = state.formattedTime
                            )
                            WalkRecordItem(
                                recordTitle = R.string.course_record_step,
                                recordContent = state.stepCounterState.sessionSteps.toString()
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 12.dp)
                        ) {
                            DokiBorderButton(
                                text = "산책 중단하기",
                                enabled = true,
                                onClick = onPauseTracking,
                                modifier = Modifier
                                    .weight(1f)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            DokiButton(
                                text = "산책 종료하기",
                                enabled = true,
                                onClick = {
                                    onStopTracking()
                                },
                                modifier = Modifier
                                    .weight(1f)
                            )
                        }
                    }
                }
            }
        }

        if (!isRecording || state.isStopTracking) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .noRippleClickable { }
            )

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (state.isStopTracking) "산책을 종료하시겠어요?" else "산책이 중단되었어요",
                    textAlign = TextAlign.Center,
                    style = PawKeyTheme.typography.header2,
                    color = PawKeyTheme.colors.background
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (state.isStopTracking) "종료 후에는 산책 기록을 이어갈 수 없어요!" else "정비 후에 다시 산책을 시작해보세요!", // stop 버튼 클릭시 , pause 버튼 클릭 시
                    textAlign = TextAlign.Center,
                    style = PawKeyTheme.typography.subTitle,
                    color = PawKeyTheme.colors.background
                )
            }

            Column(
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp)
                        .navigationBarsPadding()
                ) {
                    DokiBorderButton(
                        text = if (state.isStopTracking) "아니오" else "이어서 하기",
                        enabled = true,
                        onClick = if (state.isStopTracking) onCancelStop else onStartTracking,
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.White, RoundedCornerShape(8.dp))
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    DokiButton(
                        text = if (state.isStopTracking) "예" else "산책 종료하기",
                        enabled = true,
                        onClick = {
                            if (state.isStopTracking) {
                                naverMapInstance?.takeSnapshot(false) { bitmap ->
                                    val uri = saveBitmapToCache(
                                        context = context,
                                        bitmap = bitmap,
                                        childName = "walk_snapshot_"
                                    ).getOrNull()?.toString()

                                    bitmap.recycle()

                                    onConfirmStop(uri)
                                } ?: onConfirmStop(null)
                            } else {
                                onStopTracking()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
private fun WalkCourseBottomPreview() {
    PawKeyTheme {}
}
