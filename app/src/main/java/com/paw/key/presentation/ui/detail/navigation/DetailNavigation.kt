package com.paw.key.presentation.ui.detail.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.paw.key.core.navigation.Route
import com.paw.key.presentation.ui.detail.DetailRoute
import kotlinx.serialization.Serializable
import timber.log.Timber

fun NavController.navigateDetail(
    navOptions: NavOptions?,
    postId: Int,
    routeId: Int? = null
) {
    navigate(Detail(postId, routeId), navOptions)
}

fun NavGraphBuilder.detailNavGraph(
    paddingValues: PaddingValues,
    navigateToSharedCourse: (infoRouteId: String, isShared: Boolean, postId: Int, userId: Int) -> Unit,
    navigateUp: () -> Unit
) {
    composable<Detail> {
        DetailRoute(
            paddingValues = paddingValues,
            navigateToSharedCourse = { routeId, postId, userId ->
                Timber.e("navigateToSharedCourse $it")
                navigateToSharedCourse(routeId, true, postId, userId)
            },
            navigateUp = navigateUp
        )
    }
}

@Serializable
data class Detail(
    val postId : Int,
    val routeId : Int? = null
) : Route