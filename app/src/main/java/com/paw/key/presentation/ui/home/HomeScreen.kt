package com.paw.key.presentation.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paw.key.core.designsystem.component.LoadingScreen
import com.paw.key.core.designsystem.component.routeitem.RouteItem
import com.paw.key.core.designsystem.theme.PawKeyTheme
import com.paw.key.core.extension.noRippleClickable
import com.paw.key.core.util.UiState
import com.paw.key.presentation.ui.home.component.HomeBanner
import com.paw.key.presentation.ui.home.component.HomeEmptyRoute
import com.paw.key.presentation.ui.home.component.HomeStartWalkingRow
import com.paw.key.presentation.ui.home.component.HomeTopBar
import com.paw.key.presentation.ui.home.component.HomeWalkingInfoHolder
import com.paw.key.presentation.ui.home.state.HomeState
import com.paw.key.presentation.ui.home.viewmodel.HomeViewModel
import kotlinx.collections.immutable.persistentListOf

@Composable
fun HomeRoute(
    paddingValues: PaddingValues,
    navigateToCourse: () -> Unit,
    navigateToDbti: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchPetName()
        viewModel.fetchHomeInfo()

        // Todo: 서버 부담이 있어 추후 변경하고 호출할 예정
        //viewModel.fetchHomeRoute()
    }

    when (val uiState = state) {
        is UiState.Loading -> {
            LoadingScreen()
        }
        is UiState.Failure -> { /* 에러 UI */ }
        is UiState.Empty -> { /* 빈 UI */ }
        is UiState.Success -> {
            HomeScreen(
                paddingValues = paddingValues,
                navigateToCourse = navigateToCourse,
                navigateToDbti = navigateToDbti,
                state = uiState.data
            )
        }
    }
}

@Composable
private fun HomeScreen(
    paddingValues: PaddingValues,
    navigateToCourse: () -> Unit,
    navigateToDbti: () -> Unit,
    state: HomeState,
) {
    val itemWidth = (LocalConfiguration.current.screenWidthDp.dp - 40.dp) / 2.5f

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(color = PawKeyTheme.colors.background)
            .padding(top = 16.dp)
            .padding(paddingValues)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HomeTopBar(
            homeWeatherModel = state.homeInfo,
            onLocationClick = {},
            modifier = Modifier
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        HomeWalkingInfoHolder(
            walkingInfo = state.walkingInfo,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        HomeStartWalkingRow(
            petName = state.petName,
            onClick = navigateToCourse,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        HomeBanner(
            modifier = Modifier
                .noRippleClickable(
                    onClick = navigateToDbti
                )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "인기있는 산책 루트 추천",
            style = PawKeyTheme.typography.header3,
            color = PawKeyTheme.colors.contents,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (state.walkingPopularData.isEmpty()) {
            HomeEmptyRoute(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
        } else {
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(
                    items = state.walkingPopularData,
                    key = { _, item -> item.postId }
                ) { _, item ->
                    RouteItem(
                        routeTitle = item.title,
                        routeTime = item.duration.toString(),
                        routeDate = item.date,
                        routeImage = item.imageUrl!!,
                        location = item.regionName,
                        onClick = {},
                        onClickHeart = {},
                        modifier = Modifier.width(itemWidth)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "비슷한 이용자 루트 추천",
            style = PawKeyTheme.typography.header3,
            color = PawKeyTheme.colors.contents,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (state.walkingRecommendedData.isEmpty()) {
            HomeEmptyRoute(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
        } else {
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(
                    items = state.walkingRecommendedData,
                    key = { _, item -> item.postId }
                ) { _, item ->
                    RouteItem(
                        routeTitle = item.title,
                        routeTime = item.duration.toString(),
                        routeDate = item.date,
                        routeImage = item.imageUrl!!,
                        location = item.regionName,
                        onClick = {},
                        onClickHeart = {},
                        modifier = Modifier.width(itemWidth)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview
@Composable
private fun HomePreview() {
    PawKeyTheme {
        HomeScreen(
            paddingValues = PaddingValues(),
            navigateToCourse = {},
            navigateToDbti = {},
            state = HomeState(
                walkingRecommendedData = persistentListOf()
            )
        )
    }
}
