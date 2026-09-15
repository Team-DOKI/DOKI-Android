package com.paw.key.presentation.ui.course.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.paw.key.presentation.ui.course.walkcourse.WalkCourseRoute
import com.paw.key.presentation.ui.course.walkcourse.walkcomplete.WalkCompleteRoute
import com.paw.key.presentation.ui.course.walkcourse.walkprepare.WalkPrepareRoute
import kotlinx.serialization.Serializable

@Serializable
data object WalkCourseGraph : WalkRoute

fun NavGraphBuilder.walkCourseGraph(
    paddingValues: PaddingValues,
    navController: NavController,
    navigateSharedReview : (Int, Boolean, Int, Int) -> Unit,
    navigateWalkReviewWithId : (Int, Int) -> Unit = {_, _ ->} // routeId, routeImageId를 가지고 review로
) {
    navigation<WalkCourseGraph>(
        startDestination = WalkPrepare
    ) {
        composable<WalkPrepare> {
            WalkPrepareRoute(
                paddingValues = paddingValues,
                navigateWalkCourse = {
                    navController.navigateWalkCourse(routeId = it)
                }
            )
        }

        composable<WalkCourse> {
            WalkCourseRoute(
                paddingValues = paddingValues,
                navigateUp = navController::navigateUp,
                navigateWalkComplete = { routeId, routeImageId ->
                    navController.navigateWalkComplete(
                        routeId = routeId,
                        routeImageId = routeImageId
                    )
                },
                navigateSharedReview = navigateSharedReview
            )
        }

        composable<WalkComplete> {
            WalkCompleteRoute(
                paddingValues = paddingValues,
                navigateReview = { routeId, routeImageId ->
                    navigateWalkReviewWithId(routeId, routeImageId)
                }
            )
        }
    }
}
