package com.paw.key.presentation.ui.splash.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.paw.key.core.navigation.Route
import com.paw.key.presentation.ui.splash.SplashRoute
import kotlinx.serialization.Serializable

fun NavController.navigateSplash(
    navOptions: NavOptions?,
) {
    navigate(Splash, navOptions)
}

fun NavGraphBuilder.splashNavGraph(
    paddingValues: PaddingValues,
    navigateLogin: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToSignUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    composable<Splash> {
        SplashRoute(
            paddingValues = paddingValues,
            navigateLogin = navigateLogin,
            navigateToHome = navigateToHome,
            navigateToSignUp = navigateToSignUp,
            modifier = modifier
        )
    }
}

@Serializable
data object Splash : Route