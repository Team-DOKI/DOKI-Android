package com.paw.key.presentation.ui.course.walkcourse.walkcomplete

import android.view.Gravity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.PathOverlay
import com.naver.maps.map.compose.rememberCameraPositionState
import com.paw.key.R
import com.paw.key.core.designsystem.component.DokiButton
import com.paw.key.core.designsystem.component.TopBar
import com.paw.key.core.designsystem.component.UrlImage
import com.paw.key.core.designsystem.theme.PawKeyTheme
import com.paw.key.presentation.ui.course.walkcourse.component.WalkRecordItem
import com.paw.key.presentation.ui.course.walkcourse.walkcomplete.model.WalkFinishModel
import com.paw.key.presentation.ui.course.walkcourse.walkcomplete.state.WalkCompleteState
import java.util.Locale

@Composable
fun WalkCompleteRoute(
    paddingValues: PaddingValues,
    navigateReview: (routeId: Int, routeImageId: Int) -> Unit = {_, _ ->},
    viewModel: WalkCompleteViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchWalkComplete()
    }

    WalkCompleteScreen(
        paddingValues = paddingValues,
        state = state,
        navigateReview = {
            navigateReview(state.walkCompleteUserInfo.routeId, state.routeImageId)
        }
    )
}

@OptIn(ExperimentalNaverMapApi::class)
@Composable
private fun WalkCompleteScreen(
    paddingValues: PaddingValues,
    state: WalkCompleteState,
    navigateReview: () -> Unit = {}
) {
    val cameraPositionState = rememberCameraPositionState()
    val coordinates = state.walkCompleteMapInfo.coordinates

    LaunchedEffect(coordinates) {
        if (coordinates.size >= 2) {
            val startLat = coordinates[0][1]
            val startLng = coordinates[0][0]
            val startLatLng = LatLng(startLat, startLng)

            cameraPositionState.move(CameraUpdate.scrollTo(startLatLng))
            cameraPositionState.move(CameraUpdate.zoomTo(16.0))
        }
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(color = PawKeyTheme.colors.background)
    ) {
        TopBar(
            title = "산책 완료",
            isBackVisible = false
        )

        Spacer(modifier = Modifier.height(22.dp))

        Column (
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
                .dropShadow(
                    shape = RoundedCornerShape(16.dp),
                    shadow = Shadow(
                        radius = 4f.dp,
                        alpha = 0.25f,
                        color = Color(0xff000000),
                    )
                )
                .background(Color.White, RoundedCornerShape(16.dp))
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 프로필사진
                UrlImage(
                    url = state.walkCompleteUserInfo.petProfile.petImage,
                    modifier = Modifier
                        .size(36.dp)
                        .aspectRatio(1f)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    isUserIcon = true
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = state.walkCompleteUserInfo.petProfile.petName,
                        style = PawKeyTheme.typography.subTitle,
                        color = PawKeyTheme.colors.contents
                    )

                    Text(
                        text = state.walkCompleteUserInfo.walkInfo.startedAt,
                        style = PawKeyTheme.typography.subButtonDefault,
                        color = PawKeyTheme.colors.contents
                    )
                }
            }

            // 지도 사진
            NaverMap (
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    logoGravity = Gravity.TOP or Gravity.END,
                    isZoomControlEnabled = false,
                    isLogoClickEnabled = true
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                if (coordinates.size >= 2) {
                    PathOverlay(
                        coords = coordinates.map { LatLng(it[1], it[0]) },
                        width = 5.dp,
                        color = PawKeyTheme.colors.primary,
                        outlineWidth = 0.dp
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                with(state.walkCompleteFinishInfo) {
                    val formattedDistance = String.format(Locale.getDefault(), "%.2f", distance / 1000.0)

                    val totalSeconds = duration / 1000
                    val hours = totalSeconds / 3600
                    val minutes = (totalSeconds % 3600) / 60
                    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d",hours, minutes)

                    WalkRecordItem(
                        recordTitle = R.string.course_record_distance,
                        recordContent = formattedDistance
                    )
                    WalkRecordItem(
                        recordTitle = R.string.course_record_time,
                        recordContent = formattedTime
                    )
                    WalkRecordItem(
                        recordTitle = R.string.course_record_step,
                        recordContent = stepCount.toString()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(43.dp))

        DokiButton(
            text = "후기 작성하기",
            enabled = true,
            onClick = navigateReview,
            modifier = Modifier
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview
@Composable
private fun WalkCompletePreview() {
    PawKeyTheme {
        WalkCompleteScreen(
            paddingValues = PaddingValues(),
            state = WalkCompleteState(
                walkCompleteFinishInfo = WalkFinishModel(
                    distance = 1000,
                    duration = 1000,
                    stepCount = 1000
                )
            )
        )
    }
}
