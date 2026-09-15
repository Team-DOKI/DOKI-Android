package com.paw.key.presentation.ui.dbti.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.paw.key.presentation.ui.dbti.StartScreen
import com.paw.key.presentation.ui.dbti.result.ResultScreen
import com.paw.key.presentation.ui.dbti.test.TestScreen
import com.paw.key.presentation.ui.dbti.viewmodel.DbtiViewModel
import kotlinx.serialization.Serializable

fun NavController.navigateDbtiStart(
    showSkip: Boolean = false,
    navOptions: NavOptions? = null
) {
    navigate(DbtiStart(showSkip), navOptions)
}

fun NavController.navigateDbtiTest(navOptions: NavOptions? = null) {
    navigate(DbtiTest, navOptions)
}

fun NavController.navigateDbtiResult(navOptions: NavOptions? = null) {
    navigate(DbtiResult, navOptions)
}

fun NavGraphBuilder.dbtiNavGraph(
    navController: NavController,
    navigateUp: () -> Unit,
    navigateHome: () -> Unit,
) {
    composable<DbtiStart> { backStackEntry ->
        val args = backStackEntry.toRoute<DbtiStart>()
        val viewModel = hiltViewModel<DbtiViewModel>(backStackEntry)
        StartScreen(
            navigateUp = navigateUp,
            navigateToTest = {
                viewModel.resetTest()
                navController.navigateDbtiTest()
            },
            showSkipButton = args.showSkip,
            onSkip = if (args.showSkip) navigateHome else null
        )
    }

    composable<DbtiTest> { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry<DbtiStart>()
        }
        val viewModel = hiltViewModel<DbtiViewModel>(parentEntry)
        TestScreen(
            viewModel = viewModel,
            onBackClick = navigateUp,
            navigateToResult = {
                val options = navOptions {
                    popUpTo<DbtiTest> {
                        inclusive = true
                    }
                    launchSingleTop = true
                }

                navController.navigateDbtiResult(options)
            }
        )
    }

    composable<DbtiResult> { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry<DbtiStart>()
        }
        val viewModel = hiltViewModel<DbtiViewModel>(parentEntry)
        val resultUiModel by viewModel.resultUiModel.collectAsStateWithLifecycle()

        resultUiModel?.let { model ->
            ResultScreen(
                resultUiModel = model,
                onRetakeTest = {
                    viewModel.resetTest()
                    navController.navigateDbtiTest()
                },
                onGoHome = navigateHome,
                navigateUp = {
                    navController.popBackStack()
                }
            )
        }
    }

}

@Serializable
data class DbtiStart(
    val showSkip: Boolean = false
)

@Serializable
data object DbtiTest

@Serializable
data object DbtiResult