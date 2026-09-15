package com.paw.key.presentation.ui.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.paw.key.presentation.ui.community.navigation.communityNavGraph
import com.paw.key.presentation.ui.course.navigation.walkCourseGraph
import com.paw.key.presentation.ui.course.walkreview.navigation.WalkReview
import com.paw.key.presentation.ui.course.walkreview.navigation.walkReviewNavGraph
import com.paw.key.presentation.ui.dbti.navigation.dbtiNavGraph
import com.paw.key.presentation.ui.detail.navigation.detailNavGraph
import com.paw.key.presentation.ui.home.navigation.homeNavGraph
import com.paw.key.presentation.ui.login.navigation.loginNavGraph
import com.paw.key.presentation.ui.mypage.main.navigation.myPageNavGraph
import com.paw.key.presentation.ui.mypage.route.courseinfo.navigation.courseInfoNavGraph
import com.paw.key.presentation.ui.mypage.route.petinfo.navigation.petProfileListNavGraph
import com.paw.key.presentation.ui.mypage.route.petinfo.navigation.petProfileNavGraph
import com.paw.key.presentation.ui.mypage.route.userinfo.navigation.userProfileNavGraph
import com.paw.key.presentation.ui.onboard.navigation.onboardingNavGraph
import com.paw.key.presentation.ui.region.navigation.regionalNavGraph
import com.paw.key.presentation.ui.signup.navigation.signUpNavGraph
import com.paw.key.presentation.ui.splash.navigation.splashNavGraph

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun PawKeyNavHost(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val clearStackNavOptions = remember {
        navOptions {
            popUpTo(0) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navigator.navController,
        startDestination = navigator.startDestination,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(durationMillis = 300)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(durationMillis = 300)
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(durationMillis = 300)
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(durationMillis = 300)
            )
        },
    ) {
        homeNavGraph(
            paddingValues = paddingValues,
            navigateUp = navigator::navigateUp,
            navigateToCourse = navigator::navigateWalkPrepare,
            navigateToDbti = navigator::navigateDbtiStart
        )

        walkCourseGraph(
            paddingValues = paddingValues,
            navController = navigator.navController,
            navigateSharedReview = { routeId, isShared, postId, userId ->
                val options = navOptions {
                    popUpTo<WalkReview> {
                        inclusive = true
                    }
                    launchSingleTop = true
                }

                navigator.navigateWalkReview(
                    routeId = routeId,
                    isShared = isShared,
                    navOptions = options,
                    postId = postId,
                    userId = userId
                )
            },
            navigateWalkReviewWithId = { routeId, routeImageId ->
                val options = navOptions {
                    popUpTo<WalkReview> {
                        inclusive = true
                    }
                    launchSingleTop = true
                }

                navigator.navigateWalkReview(
                    routeId = routeId,
                    routeImageId = routeImageId,
                    navOptions = options
                )
            }
        )

        walkReviewNavGraph(
            paddingValues = paddingValues,
            navigateHome = navigator::navigateHome,
            navigateWalkDetail = { postId, routeId ->
                navigator.navigateDetail(
                    postId = postId,
                    routeId = routeId,
                    navOptions = null
                )
            },
        )

        communityNavGraph(
            paddingValues = paddingValues,
            navigateDetail = {
                navigator.navigateDetail(
                    postId = it,
                    navOptions = null
                )
            }
        )

        detailNavGraph(
            paddingValues = paddingValues,
            navigateToSharedCourse = { infoRouteId, isShared, postId, userId ->
                navigator.navigateWalkCourse(
                    infoRouteId = infoRouteId.toInt(),
                    isShared = isShared,
                    postId = postId,
                    userId = userId
                )
            },
            navigateUp = navigator::navigateUp
        )

        dbtiNavGraph(
            navController = navigator.navController,
            navigateUp = navigator::navigateUp,
            navigateHome = navigator::navigateHome
        )

        myPageNavGraph(
            paddingValues = paddingValues,
            navigateUp = navigator::navigateUp,
            navigatePetProfile = navigator::navigatePetProfile,
            navigateCourseInfo = { courseType ->
                navigator.navigateCourseInfo(courseType)
            },
            navigatePetProfileList = navigator::navigatePetProfileList,
            navigateUserProfile = navigator::navigateUserProfile,
            navigateToRegionSetting = navigator::navigateRegional,
            navigateLogin = {
                navigator.navigateLogin(clearStackNavOptions)
            },
            navigateDbtiStart = navigator::navigateDbtiStart
        )

        courseInfoNavGraph(
            navigateUp = navigator::navigateUp,
            navigateToDetail = {
                navigator.navigateDetail(
                    postId = it,
                    navOptions = null
                )
            }
            /*navigateDetail = {
                navigator.navController.navigateCourse(index = 1, navOptions = null)
            },*/
            /*navigateToSharedWalk = { routeId, pageId ->
                *//*navigator.navigateSharedWalkCourse(
                    routeId = routeId,
                    pageId = pageId
                )*//*
            },
            modifier = modifier*/
        )

        userProfileNavGraph(
            paddingValues = paddingValues,
            navigateUp = navigator::navigateMyPage,
            navigateNext = {},
            snackBarHostState = snackbarHostState
        )

        petProfileNavGraph(
            navigateUp = navigator::navigateUp,
        )

        petProfileListNavGraph(
            navigateUp = navigator::navigateUp,
            navigatePetProfile = navigator::navigatePetProfile,
        )

        splashNavGraph(
            paddingValues = paddingValues,
            navigateLogin = {
                val options = navOptions {
                    popUpTo(0) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
                navigator.navigateOnboarding(navOptions = options)
            },
            navigateToSignUp = {
                navigator.navigateSignUp(clearStackNavOptions)
            },
            navigateToHome = {
                navigator.navigateHome(clearStackNavOptions)
            },
        )

        onboardingNavGraph(
            paddingValues = paddingValues,
            navigateUp = navigator::navigateUp,
            navigateNext = navigator::navigateLogin,
            navigateSignUp = navigator::navigateLogin,
            snackBarHostState = snackbarHostState
        )


        loginNavGraph(
            paddingValues = paddingValues,
            navigateSignUp = {
                navigator.navigateSignUp(clearStackNavOptions)
            },
            navigateHome = {
                navigator.navigateHome(clearStackNavOptions)
            },
        )

        regionalNavGraph(
            paddingValues = paddingValues,
            navigateUp = navigator::navigateUp,
            navigateNext = navigator::navigateMyPage,
            snackBarHostState = snackbarHostState,
        )

        signUpNavGraph(
            navigateUp = {
                val options = navOptions {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
                navigator.navigateLogin(options)
            },
            navigateToDBTI = {
                val options = navOptions {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
                navigator.navigateDbtiStart(showSkip = it, navOptions = options)
            }
        )
    }
}