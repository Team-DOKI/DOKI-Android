package com.paw.key.presentation.ui.course.navigation

import com.paw.key.core.navigation.MainTabRoute
import kotlinx.serialization.Serializable

sealed interface WalkRoute : MainTabRoute

@Serializable
data object WalkPrepare: WalkRoute

@Serializable
data class WalkCourse(
    val routeId: String? = null, // 세션용
    val infoRouteId: Int? = null, // 일반용 - 좌표 조회, 리뷰 등록, detail 조회 등
    val isShared: Boolean = false,
    val postId: Int? = null, // 공유된 루트 산책 작성자 조회 시 필요
    val userId: Int? = null // 공유된 루트 산책 후 리뷰 전송 시 필요
): WalkRoute

@Serializable
data class WalkComplete(
    val routeId: Int,
    val routeImageId: Int
): WalkRoute

