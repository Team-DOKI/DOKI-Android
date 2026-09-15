package com.paw.key.presentation.ui.mypage.route.courseinfo.model

import androidx.compose.runtime.Immutable
import com.paw.key.core.util.UiState
import com.paw.key.domain.entity.mypage.ReviewPostEntity
import com.paw.key.domain.entity.mypage.RoutePostEntity

enum class CourseType(val courseType: String) {
    MyCourse(courseType = "내가 기록한 산책"),
    AllCourse(courseType = "저장 목록"),
    ReviewCourse(courseType = "내가 남긴 후기"),
}

data class CourseData(
    val postId: Int = -1,
    val location: String = "",
    val title: String = "",
    val imageUrl: String = "",
    val time: String = "",
    val date: String = "",
    val isLiked: Boolean = false,
    val categoryOptionSummary: List<String> = emptyList(),
)

@Immutable
data class CourseInfoState(
    val courses: UiState<List<CourseData>> = UiState.Loading,
)

sealed interface CourseInfoSideEffect {
    data class ShowSnackBar(val message: String) : CourseInfoSideEffect
}


fun RoutePostEntity.toUiModel() = CourseData(
    postId = postId,
    location = regionName,
    title = title,
    imageUrl = imageUrl,
    time = "${durationMinutes}분",
    date = date.split("T").first().replace("-", "/"),
    isLiked = isLiked,
)

fun ReviewPostEntity.toCourseData() = CourseData(
    postId = postId,
    location = regionName,
    title = title,
    imageUrl = "",
    time = "",
    date = date.split("T").first().replace("-", "/"),
    categoryOptionSummary = categoryOptionSummary,
)
