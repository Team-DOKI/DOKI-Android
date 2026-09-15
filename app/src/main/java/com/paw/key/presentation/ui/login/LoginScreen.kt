package com.paw.key.presentation.ui.login

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.paw.key.R
import com.paw.key.core.designsystem.component.LoadingScreen
import com.paw.key.core.designsystem.theme.PawKeyTheme
import com.paw.key.presentation.ui.login.component.LoginSocialButton
import com.paw.key.presentation.ui.login.state.LoginSideEffect
import com.paw.key.presentation.ui.login.viewmodel.LoginViewModel

@Composable
fun LoginRoute(
    paddingValues: PaddingValues,
    navigateHome: () -> Unit,
    navigateSignUp: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifeCycle = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.sideEffect.flowWithLifecycle(lifeCycle.lifecycle)
            .collect { sideEffect ->
                when (sideEffect) {
                    is LoginSideEffect.NavigateToHome -> navigateHome()
                    is LoginSideEffect.NavigateToSignUp -> navigateSignUp()
                    is LoginSideEffect.ShowSnackBar -> {
                        Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }

            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LoginScreen(
            paddingValues = paddingValues,
            onGoogleSignIn = {
                viewModel.onGoogleSignIn(context = context as Activity)
            },
            onKakaoSignIn = {
                viewModel.onKakaoSignIn(context = context)
            },
            navigateHome = navigateHome
        )

        if (state.isLoading) {
            LoadingScreen()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoginScreen(
    paddingValues: PaddingValues,
    navigateHome: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onKakaoSignIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PawKeyTheme.colors.background)
            .padding(paddingValues)
    ) {
        Column(
            modifier = Modifier
                .matchParentSize()
                .statusBarsPadding()
                .background(PawKeyTheme.colors.white1)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Spacer(modifier = Modifier.height(130.dp))

            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_login_title_logo),
                contentDescription = stringResource(id = R.string.ic_login_main_logo),
                tint = PawKeyTheme.colors.primary,
                modifier = Modifier
                    .clickable(onClick = navigateHome)
                    .padding(start = 19.dp)
            )

            Text(
                text = stringResource(R.string.ic_login_main_text),
                color = PawKeyTheme.colors.contents,
                style = PawKeyTheme.typography.header3,
                modifier = Modifier.padding(start = 19.dp)
            )

            Spacer(modifier = Modifier.height(310.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(R.drawable.img_login_sub),
                    contentDescription = stringResource(R.string.ic_login_sub_image),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .padding(horizontal = 100.dp)
                )

                LoginSocialButton(
                    logo = R.drawable.ic_login_kakao,
                    loginText = stringResource(R.string.ic_login_kakao),
                    onClick = {
                        onKakaoSignIn()
                    },
                    modifier = Modifier
                        .background(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFEE500)
                        )
                )

                LoginSocialButton(
                    logo = R.drawable.ic_login_google,
                    loginText = stringResource(R.string.ic_login_google),
                    onClick = {
                        onGoogleSignIn()
                    },
                    modifier = Modifier
                        .background(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF2F2F2)
                        )
                )
            }

            Spacer(modifier = Modifier.height(34.dp))
        }

        Image(
            painter = painterResource(R.drawable.img_login_main),
            contentDescription = stringResource(R.string.ic_login_main_image),
            modifier = Modifier
                .size(370.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 10.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewLoginScreen() {
    PawKeyTheme {
        LoginScreen(
            paddingValues = PaddingValues(),
            onGoogleSignIn = {},
            onKakaoSignIn = {},
            navigateHome = {}
        )
    }
}
