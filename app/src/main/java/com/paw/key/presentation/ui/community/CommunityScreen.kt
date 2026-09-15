package com.paw.key.presentation.ui.community

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.layout.LazyLayoutCacheWindow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paw.key.R
import com.paw.key.core.designsystem.component.TopBar
import com.paw.key.core.designsystem.component.routeitem.RouteItem
import com.paw.key.core.designsystem.theme.PawKeyTheme
import com.paw.key.core.extension.noRippleClickable
import com.paw.key.presentation.ui.community.component.CommunityTopImageHolder
import com.paw.key.presentation.ui.community.component.FilterScreen
import com.paw.key.presentation.ui.community.model.SortedType
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import timber.log.Timber

/**
 * [루트 추천] 화면
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommunityRoute( // 루트 추천
    paddingValues: PaddingValues,
    navigateDetail: (Int) -> Unit,
    viewModel: CommunityViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var isFilterSheetVisible by remember { mutableStateOf(false) }
    val gridState = rememberLazyGridState(
        cacheWindow = LazyLayoutCacheWindow(
            aheadFraction = 0.7f,
            behindFraction = 0.3f
        )
    )

    BackHandler {
        if (isFilterSheetVisible) {
            isFilterSheetVisible = false
            viewModel.onRefreshFilter()
        }
    }

    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .collectLatest { lastIndex ->
                if (lastIndex != null && lastIndex >= state.communityRouteList.size - 3) {
                    Timber.e("onloadmore")
                    viewModel.onLoadMore()
                }
            }
    }

    if (isFilterSheetVisible) {
        FilterScreen(
            paddingValues = paddingValues,
            state = state,
            onFilterClick = viewModel::onFilterClick,
            onCompleted = {
                isFilterSheetVisible = false
                viewModel.fetchPosts()
            },
            onBackClick = { isFilterSheetVisible = false },
            onClickSuffix = {
                viewModel.onRefreshFilter()
            }
        )
    } else {
        CommunityScreen(
            paddingValues = paddingValues,
            state = state,
            gridState = gridState,
            onShowFilterSheet = { isFilterSheetVisible = true },
            onClickSort = viewModel::onSortTypeChanged,
            onClickHeart = viewModel::addRouteLike,
            onClickRoute = navigateDetail
        )
    }
}

@Composable
fun CommunityScreen(
    paddingValues: PaddingValues,
    state: CommunityState,
    gridState: LazyGridState,
    onShowFilterSheet: () -> Unit = {},
    onClickSort: (SortedType) -> Unit = {},
    onClickHeart: (Int) -> Unit = {},
    onClickRoute: (Int) -> Unit = {}
) {
    val filterList = state.filterUiModel.allCategories.map { it.name }.toImmutableList()
    var isSortMenuExpanded by remember { mutableStateOf(false) }

    val topImageHeight = 140.dp
    val topImageHeightPx = with(LocalDensity.current) { topImageHeight.toPx() }
    var topImageOffset by remember { mutableFloatStateOf(0f) }
    val currentImageHeight = with(LocalDensity.current) {
        (topImageHeightPx + topImageOffset).coerceAtLeast(0f).toDp()
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                return if (delta < 0) {
                    // 아래로 스크롤 → TopImage 먼저 접기
                    val newOffset = (topImageOffset + delta).coerceIn(-topImageHeightPx, 0f)
                    val consumed = newOffset - topImageOffset
                    topImageOffset = newOffset
                    Offset(0f, consumed)
                } else {
                    // 위로 스크롤 → grid가 처리하도록 소비 안 함
                    Offset.Zero
                }
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val delta = available.y
                return if (delta > 0) {
                    // grid가 더 이상 위로 스크롤 못할 때 (available.y > 0) → TopImage 펼치기
                    val newOffset = (topImageOffset + delta).coerceIn(-topImageHeightPx, 0f)
                    val consumed2 = newOffset - topImageOffset
                    topImageOffset = newOffset
                    Offset(0f, consumed2)
                } else {
                    Offset.Zero
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = PawKeyTheme.colors.background)
            .padding(paddingValues),
    ) {
        TopBar(
            title = "루트 추천",
            onBackClick = {},
            isBackVisible = false,
            thickness = 2
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .nestedScroll(nestedScrollConnection)
        ) {
            // 접히는 TopImage
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(currentImageHeight)
                    .clip(RectangleShape)
            ) {
                CommunityTopImageHolder(imageList = persistentListOf("https://picsum.photos/200/300", "https://picsum.photos/200/300"))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = (topImageHeight.value + topImageOffset / LocalDensity.current.density).coerceAtLeast(0f).dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PawKeyTheme.colors.background)
                        .padding(horizontal = 16.dp)
                        .padding(top = 19.dp, bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = if (state.selectedOptionIds.isNotEmpty()) {
                            ImageVector.vectorResource(R.drawable.ic_course_option_selected_filter)
                        } else {
                            ImageVector.vectorResource(R.drawable.ic_course_optin_filter)
                        },
                        contentDescription = "filter",
                        tint = Color.Unspecified,
                        modifier = Modifier.noRippleClickable(onClick = onShowFilterSheet)
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filterList.size) { index ->
                            val filter = filterList[index]
                            val isSelected = state.selectedOptionIds.keys.any { categoryId ->
                                state.filterUiModel.allCategories
                                    .firstOrNull { it.id == categoryId }?.name == filter
                            }

                            Box(
                                modifier = Modifier
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) PawKeyTheme.colors.primary
                                        else PawKeyTheme.colors.defaultButton,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .background(
                                        color = if (isSelected) PawKeyTheme.colors.opacity5Primary
                                        else PawKeyTheme.colors.background,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 9.dp)
                            ) {
                                Text(
                                    text = filter,
                                    style = PawKeyTheme.typography.subButtonDefault,
                                    color = if (isSelected) PawKeyTheme.colors.primary
                                    else PawKeyTheme.colors.defaultMiddle
                                )
                            }
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "${state.communityRouteList.size}개의 루트",
                        style = PawKeyTheme.typography.subButtonDefault,
                        color = PawKeyTheme.colors.defaultDark
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.noRippleClickable { isSortMenuExpanded = true }
                        ) {
                            Text(
                                text = state.selectedSortedType.label,
                                style = PawKeyTheme.typography.subButtonDefault,
                                color = PawKeyTheme.colors.defaultDark
                            )
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_down),
                                contentDescription = "sort",
                                tint = Color.Unspecified
                            )
                        }

                        DropdownMenu(
                            expanded = isSortMenuExpanded,
                            onDismissRequest = { isSortMenuExpanded = false },
                            modifier = Modifier.background(PawKeyTheme.colors.background)
                        ) {
                            SortedType.entries.forEach { option ->
                                DropdownMenuItem(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (option == state.selectedSortedType)
                                                PawKeyTheme.colors.opacity5Primary
                                            else PawKeyTheme.colors.background
                                        ),
                                    text = {
                                        Text(
                                            text = option.label,
                                            style = PawKeyTheme.typography.subButtonDefault,
                                            color = if (option == state.selectedSortedType)
                                                PawKeyTheme.colors.primary
                                            else PawKeyTheme.colors.defaultMiddle
                                        )
                                    },
                                    onClick = {
                                        onClickSort(option)
                                        isSortMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.communityRouteList.size) { index ->
                        RouteItem(
                            routeTitle = state.communityRouteList[index].title,
                            routeTime = state.communityRouteList[index].duration.toString(),
                            routeDate = state.communityRouteList[index].date,
                            location = state.communityRouteList[index].regionName,
                            routeImage = state.communityRouteList[index].imageUrl ?: "",
                            isLiked = state.communityRouteList[index].isLiked,
                            onClickHeart = {
                                onClickHeart(state.communityRouteList[index].postId)
                            },
                            onClick = {
                                onClickRoute(state.communityRouteList[index].postId)
                            }
                        )
                    }
                }
            }
        }
    }
}



@Preview
@Composable
private fun CommunityScreenPreview() {
    PawKeyTheme {
        CommunityScreen(
            paddingValues = PaddingValues(),
            state = CommunityState(),
            gridState = rememberLazyGridState()
        )
    }
}
