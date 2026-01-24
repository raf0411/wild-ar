package android.app.faunadex.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import android.app.faunadex.presentation.animalDetail.AnimalDetailScreen
import android.app.faunadex.presentation.ar.ArScreen
import android.app.faunadex.presentation.auth.login.LoginScreen
import android.app.faunadex.presentation.auth.onboarding.OnboardingScreen
import android.app.faunadex.presentation.auth.register.RegisterScreen
import android.app.faunadex.presentation.dashboard.DashboardScreen
import android.app.faunadex.presentation.profile.ChangePasswordScreen
import android.app.faunadex.presentation.profile.EditProfileScreen
import android.app.faunadex.presentation.profile.ProfileScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onLoginClick = {
                    navController.navigate(Screen.Login.route) {
                        launchSingleTop = true
                    }
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToAnimalDetail = { animalId ->
                    navController.navigate(Screen.AnimalDetail.createRoute(animalId))
                }
            )
        }

        composable(
            route = Screen.AnimalDetail.route,
            arguments = listOf(
                navArgument("animalId") {
                    type = NavType.StringType
                }
            )
        ) {
            AnimalDetailScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToAr = {
                    navController.navigate(Screen.AR.route)
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToQuiz = {
                    // TODO: Navigate to quiz when screen is implemented
                },
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToEditProfile = {
                    navController.navigate(Screen.EditProfile.route)
                },
                onNavigateToChangePassword = {
                    navController.navigate(Screen.ChangePassword.route)
                }
            )
        }

        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.ChangePassword.route) {
            ChangePasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.AR.route) {
            ArScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

