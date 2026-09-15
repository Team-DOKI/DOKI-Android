package com.paw.key.presentation.ui.mypage.route.courseinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paw.key.core.designsystem.component.TopBar
import com.paw.key.core.designsystem.component.routeitem.RouteItem
import com.paw.key.core.designsystem.theme.PawKeyTheme
import com.paw.key.core.extension.collectSideEffect
import com.paw.key.core.util.UiState
import com.paw.key.presentation.ui.mypage.route.courseinfo.component.MyReviewCard
import com.paw.key.presentation.ui.mypage.route.courseinfo.model.CourseData
import com.paw.key.presentation.ui.mypage.route.courseinfo.model.CourseInfoSideEffect
import com.paw.key.presentation.ui.mypage.route.courseinfo.model.CourseType
import com.paw.key.presentation.ui.mypage.route.courseinfo.viewmodel.CourseInfoViewModel

@Composable
fun CourseInfoRoute(
    navigateUp: () -> Unit,
    navigateToDetail: (Int) -> Unit,
    courseType: CourseType,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: CourseInfoViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    viewModel.sideEffect.collectSideEffect {
        when (it) {
            is CourseInfoSideEffect.ShowSnackBar -> snackbarHostState.showSnackbar(it.message)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchCourses()
    }

    CourseInfoScreen(
        title      = viewModel.courseType.courseType,
        uiState    = state.courses,
        courseType = viewModel.courseType,
        navigateUp = navigateUp,
        navigateToDetail = navigateToDetail,
    )
}

@Composable
fun CourseInfoScreen(
    title: String,
    courseType: CourseType,
    uiState: UiState<List<CourseData>>,
    navigateUp: () -> Unit,
    navigateToDetail: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PawKeyTheme.colors.background),
    ) {
        TopBar(title = title, onBackClick = navigateUp)

        HorizontalDivider(
            modifier  = Modifier.fillMaxWidth(),
            thickness = 2.dp,
            color     = PawKeyTheme.colors.defaultButton,
        )

        when (uiState) {
            is UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        color = PawKeyTheme.colors.primary
                    )
                }
            }

            is UiState.Empty -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "저장된 산책이 없어요",
                        style = PawKeyTheme.typography.body14M,
                        color = PawKeyTheme.colors.gray50,
                    )
                }
            }

            is UiState.Failure -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    // TODO: 뭘로 만들지
                }
            }

            is UiState.Success -> {
                when (courseType) {
                    CourseType.ReviewCourse -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(20.dp),
                        ) {
                            items(items = uiState.data, key = { it.postId }) { course ->
                                MyReviewCard(
                                    course = course,
                                )
                            }
                        }
                    }
                    else -> {
                        LazyVerticalGrid(
                            modifier = Modifier.fillMaxSize(),
                            columns = GridCells.Fixed(2),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(20.dp),
                        ) {
                            items(items = uiState.data, key = { it.postId }) { course ->
                                RouteItem(
                                    location     = course.location,
                                    routeTitle   = course.title,
                                    routeImage   = course.imageUrl,
                                    routeTime    = course.time,
                                    routeDate    = course.date,
                                    onClick      = {
                                        navigateToDetail(course.postId, /*course.routeId*/)
                                    },
                                    onClickHeart = {},
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}