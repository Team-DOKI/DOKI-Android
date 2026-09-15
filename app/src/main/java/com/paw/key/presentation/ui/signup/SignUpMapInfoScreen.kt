package com.paw.key.presentation.ui.signup

import android.view.Gravity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.PolygonOverlay
import com.naver.maps.map.compose.rememberCameraPositionState
import com.paw.key.core.designsystem.component.PawkeyButton
import com.paw.key.core.designsystem.theme.PawKeyTheme
import com.paw.key.presentation.ui.region.state.DrawType
import kotlinx.collections.immutable.ImmutableList


@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun SignUpMapInfoScreen(
    type: DrawType,
    regionCoordinates: ImmutableList<ImmutableList<LatLng>>,
    entireCoordinates: ImmutableList<LatLng>,
    regionName: String,
    onClickButton: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cameraPositionState = rememberCameraPositionState()
    val bottomPanelHeightPx = remember {
        mutableIntStateOf(0)
    }

    val density = LocalDensity.current

    LaunchedEffect(entireCoordinates.size) {
        if (entireCoordinates.size >= 2 && bottomPanelHeightPx.intValue > 0) {
            val bounds = LatLngBounds.from(entireCoordinates)

            val bottomPadding =
                bottomPanelHeightPx.intValue + with(density) { 100.dp.roundToPx() }

            cameraPositionState.move(
                CameraUpdate.fitBounds(bounds, 100, 100, 100, bottomPadding)
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        NaverMap(
            modifier = Modifier
                .fillMaxSize()
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
                    bottomPanelHeightPx.intValue = size.height
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
                    text = regionName,
                    style = PawKeyTheme.typography.header3,
                    color = PawKeyTheme.colors.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "선택한 산책 지역은 ${regionName}이에요.\n이 위치로 산책 지역을 설정하시겠어요?",
                style = PawKeyTheme.typography.bodyDefault,
                color = PawKeyTheme.colors.gray500,
                modifier = Modifier
                    .padding(bottom = 12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            PawkeyButton(
                text = "선택",
                onClick = onClickButton,
                modifier = Modifier
                    .fillMaxWidth(),
                enabled = true,
            )
        }
    }
}