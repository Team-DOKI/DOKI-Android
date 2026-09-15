package com.paw.key.presentation.ui.home.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.paw.key.core.navigation.MainTabRoute
import com.paw.key.presentation.ui.home.HomeRoute
import kotlinx.serialization.Serializable

fun NavController.navigateHome(
    navOptions: NavOptions?,
) {
    navigate(Home, navOptions)
}

fun NavGraphBuilder.homeNavGraph(
    paddingValues: PaddingValues,
    navigateUp: () -> Unit,
    navigateToCourse: () -> Unit,
    navigateToDbti: () -> Unit,
) {
    composable<Home> {
        HomeRoute(
            paddingValues = paddingValues,
            navigateToCourse = navigateToCourse,
            navigateToDbti = navigateToDbti
        )
    }
}

@Serializable
data object Home : MainTabRoute
