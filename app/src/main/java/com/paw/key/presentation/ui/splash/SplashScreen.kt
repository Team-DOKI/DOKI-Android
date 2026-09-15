package com.paw.key.presentation.ui.splash

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.paw.key.R
import com.paw.key.core.designsystem.theme.PawKeyTheme
import com.paw.key.core.extension.collectSingleEvent
import com.paw.key.presentation.ui.splash.state.SplashSideEffect
import com.paw.key.presentation.ui.splash.viewmodel.SplashViewModel

@Preview(showBackground = true)
@Composable
private fun PreviewSplashScreen() {
    PawKeyTheme {
        SplashScreen(
            paddingValues = PaddingValues(),
            modifier = Modifier
        )
    }
}

@Composable
fun SplashRoute(
    paddingValues: PaddingValues,
    navigateLogin: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToSignUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val statusBarColor = PawKeyTheme.colors.green500

    val context = LocalContext.current
    val window = (context as? Activity)?.window
    val previousNavBarColor = remember { window?.statusBarColor }

    DisposableEffect(Unit) {
        window?.statusBarColor = statusBarColor.toArgb()

        onDispose {
            // 화면에서 벗어날 때 원래 색으로 복원
            previousNavBarColor?.let {
                window?.statusBarColor = it
            }
        }
    }

    viewModel.sideEffect.collectSingleEvent {
        when (it) {
            SplashSideEffect.NavigateToLogin -> navigateLogin()

            SplashSideEffect.NavigateToHome -> navigateToHome()

            SplashSideEffect.NavigateToSignUp -> navigateToSignUp()
        }
    }

    SplashScreen(
        paddingValues = paddingValues,
        modifier = modifier
    )
}


@Composable
fun SplashScreen(
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(color = PawKeyTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_walk_review_dialog_paw),
                contentDescription = stringResource(id = R.string.ic_logo),
                tint = PawKeyTheme.colors.primary,
                modifier = Modifier
                    .size(154.dp)
            )

            Text(
                text = stringResource(id = R.string.app_name),
                style = PawKeyTheme.typography.header1,
                color = PawKeyTheme.colors.primary,
                modifier = Modifier
                    .padding(top = 42.dp)
            )
        }
    }
}

