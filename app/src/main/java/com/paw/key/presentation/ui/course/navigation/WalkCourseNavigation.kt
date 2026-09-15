package com.paw.key.presentation.ui.course.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions

fun NavController.navigateWalkCourse(
    navOptions: NavOptions? = null,
    routeId: String?,
    infoRouteId: Int? = null,
    isShared: Boolean = false,
    postId: Int? = null,
    userId: Int? = null,
) {
    navigate(WalkCourse(routeId, infoRouteId, isShared, postId, userId), navOptions)
}

fun NavController.navigateWalkPrepare(
    navOptions: NavOptions?,
) {
    navigate(WalkPrepare, navOptions)
}

fun NavController.navigateWalkComplete( // complete 후 routeId는 int
    navOptions: NavOptions? = null,
    routeId: Int,
    routeImageId: Int
) {
    navigate(WalkComplete(routeId, routeImageId), navOptions)
}