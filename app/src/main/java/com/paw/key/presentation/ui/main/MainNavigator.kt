package com.paw.key.presentation.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.paw.key.presentation.ui.community.navigation.navigateCommunity
import com.paw.key.presentation.ui.course.navigation.navigateWalkCourse
import com.paw.key.presentation.ui.course.navigation.navigateWalkPrepare
import com.paw.key.presentation.ui.course.walkreview.navigation.navigateWalkReview
import com.paw.key.presentation.ui.dbti.navigation.navigateDbtiStart
import com.paw.key.presentation.ui.detail.navigation.navigateDetail
import com.paw.key.presentation.ui.home.navigation.navigateHome
import com.paw.key.presentation.ui.login.navigation.navigateLogin
import com.paw.key.presentation.ui.mypage.main.navigation.navigateMyPage
import com.paw.key.presentation.ui.mypage.route.courseinfo.model.CourseType
import com.paw.key.presentation.ui.mypage.route.courseinfo.navigation.navigateToCourseInfo
import com.paw.key.presentation.ui.mypage.route.petinfo.navigation.navigatePetProfile
import com.paw.key.presentation.ui.mypage.route.petinfo.navigation.navigatePetProfileList
import com.paw.key.presentation.ui.mypage.route.userinfo.navigation.navigateUserProfile
import com.paw.key.presentation.ui.onboard.navigation.navigateOnboarding
import com.paw.key.presentation.ui.region.navigation.navigateRegional
import com.paw.key.presentation.ui.signup.navigation.navigateSignUp
import com.paw.key.presentation.ui.splash.navigation.Splash

class MainNavigator(
    val navController: NavHostController,
) {
    private val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val startDestination = Splash

    val currentTab: MainTab?
        @Composable get() = MainTab.find { tab ->
            currentDestination?.hasRoute(tab::class) == true
        }

    var isRecordVisible: Boolean = false
        private set

    fun navigate(tab: MainTab) {
        val navOptions = navOptions {
            navController.currentDestination?.route?.let {
                popUpTo(it) {
                    inclusive = true
                    saveState = true
                }
            }
            launchSingleTop = true
            restoreState = true
        }

        when (tab) {
            MainTab.HOME -> navController.navigateHome(navOptions)
            MainTab.COURSE -> navController.navigateWalkPrepare(navOptions)
            MainTab.ROUTERECOMMAND -> navController.navigateCommunity(navOptions)
            MainTab.MYPAGE -> navController.navigateMyPage(navOptions)
        }
    }

    fun setOnVisibleRecord(visible: Boolean) {
        isRecordVisible = visible
    }

    /*온보딩, 로그인, */
    fun navigateOnboarding(navOptions: NavOptions? = null) {
        navController.navigateOnboarding(navOptions = navOptions)
    }

    fun navigateLogin(navOptions: NavOptions? = null) {
        navController.navigateLogin(navOptions = navOptions)
    }

    fun navigateMyPage(navOptions: NavOptions? = null) {
        navController.navigateMyPage(navOptions = navOptions)
    }


    fun navigateUserProfile(navOptions: NavOptions? = null) {
        navController.navigateUserProfile(navOptions = navOptions)
    }

    fun navigatePetProfile(navOptions: NavOptions? = null) {
        navController.navigatePetProfile(navOptions = navOptions)
    }

    fun navigatePetProfileList(navOptions: NavOptions? = null) {
        navController.navigatePetProfileList(navOptions = navOptions)
    }

    fun navigateCourseInfo(
        courseType: CourseType,
        navOptions: NavOptions? = null,
    ) {
        navController.navigateToCourseInfo(courseType = courseType, navOptions = navOptions)
    }


    fun navigateSignUp(navOptions: NavOptions? = null) {
        navController.navigateSignUp(navOptions)
    }

    fun navigateHome(navOptions: NavOptions? = null) {
        navController.navigateHome(navOptions = navOptions)
    }

    fun navigateDetail(
        navOptions: NavOptions? = null,
        postId: Int,
        routeId: Int? = null
    ) {
        navController.navigateDetail(
            navOptions = navOptions,
            postId = postId,
            routeId = routeId
        )
    }

    // walk course
    fun navigateWalkCourse(
        navOptions: NavOptions? = null,
        routeId: String? = null,
        infoRouteId: Int? = null,
        postId: Int? = null,
        userId: Int? = null,
        isShared: Boolean = false
    ) {
        navController.navigateWalkCourse(
            navOptions = navOptions,
            routeId = routeId,
            infoRouteId = infoRouteId,
            postId = postId,
            userId = userId,
            isShared = isShared
        )
    }

    fun navigateWalkReview(
        navOptions: NavOptions? = null,
        routeId: Int? = null,
        routeImageId: Int? = null,
        isShared: Boolean = false,
        postId: Int? = null,
        userId: Int? = null
    ) {
        navController.navigateWalkReview(
            navOptions = navOptions,
            routeId = routeId,
            isShared = isShared,
            routeImageId = routeImageId,
            postId = postId,
            userId = userId
        )
    }

    fun navigateWalkPrepare(
        navOptions: NavOptions? = null
    ) {
        navController.navigateWalkPrepare(
            navOptions = navOptions
        )
    }

    fun navigateUp() {
        navController.navigateUp()
    }

    fun navigateRegional(navOptions: NavOptions? = null) {
        navController.navigateRegional(navOptions)
    }

    fun navigateDbtiStart(
        showSkip: Boolean = false,
        navOptions: NavOptions? = null
    ) {
        navController.navigateDbtiStart(showSkip = showSkip, navOptions = navOptions)
    }


    @Composable
    fun showBottomBar() = MainTab.contains {
        currentDestination?.hasRoute(it::class) == true
    } && !isRecordVisible
}

@Composable
fun rememberMainNavigator(
    navController: NavHostController = rememberNavController(),
): MainNavigator = remember(navController) {
    MainNavigator(navController)
}