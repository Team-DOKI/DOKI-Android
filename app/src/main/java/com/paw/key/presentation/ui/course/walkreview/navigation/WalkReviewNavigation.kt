package com.paw.key.presentation.ui.course.walkreview.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.paw.key.core.navigation.Route
import com.paw.key.presentation.ui.course.walkreview.WalkReviewRoute
import kotlinx.serialization.Serializable

fun NavController.navigateWalkReview(
    navOptions: NavOptions?,
    routeId: Int? = null,
    routeImageId: Int? = null,
    isShared: Boolean = false,
    postId: Int? = null,
    userId: Int? = null
) {
    navigate(WalkReview(
        routeId,
        routeImageId,
        isShared = isShared,
        postId,
        userId
    ), navOptions)
}

fun NavGraphBuilder.walkReviewNavGraph(
    paddingValues: PaddingValues,
    navigateHome: () -> Unit,
    navigateWalkDetail: (postId: Int, routeId: Int) -> Unit,
) {
    composable<WalkReview> {
        WalkReviewRoute(
            paddingValues = paddingValues,
            navigateHome = navigateHome,
            navigateUp = navigateHome,
            navigateWalkDetail = navigateWalkDetail
        )
    }
}

@Serializable
data class WalkReview(
    val routeId: Int? = null,
    val routeImageId: Int? = null,
    val isShared: Boolean = false,
    val postId: Int? = null,
    val userId: Int? = null
) : Route
