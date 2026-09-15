package com.paw.key.presentation.ui.region.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.paw.key.core.navigation.Route
import com.paw.key.presentation.ui.region.RegionalManagementRoute
import kotlinx.serialization.Serializable

fun NavController.navigateRegional(
    navOptions: NavOptions?,
) {
    navigate(Regional, navOptions)
}

fun NavGraphBuilder.regionalNavGraph(
    paddingValues: PaddingValues,
    navigateUp: () -> Unit,
    navigateNext: () -> Unit, // 마이페이지로
    snackBarHostState: SnackbarHostState,
) {
    composable<Regional> {
        RegionalManagementRoute(
            paddingValues = paddingValues,
            snackBarHostState = snackBarHostState,
            navigateUp = navigateUp,
            navigateNext = navigateNext,
        )
    }
}

@Serializable
data object Regional : Route