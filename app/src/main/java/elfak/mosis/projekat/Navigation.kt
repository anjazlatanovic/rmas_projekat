package elfak.mosis.projekat

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import elfak.mosis.projekat.Screens.AllMarkerScreen
import elfak.mosis.projekat.Screens.EventsAndQuizzes.EventsAndQuizzesScreen
import elfak.mosis.projekat.Screens.HomeScreen
import elfak.mosis.projekat.Screens.Location.LocationScreen
import elfak.mosis.projekat.Screens.Login.LoginScreen
import elfak.mosis.projekat.Screens.Profil.EditProfileScreen
import elfak.mosis.projekat.Screens.Profil.ProfileScreen
import elfak.mosis.projekat.Screens.Ranking.RankingScreen
import elfak.mosis.projekat.Screens.Signup.PrivacyScreen
import elfak.mosis.projekat.Screens.Signup.SignUpScreen

sealed class Route {
    data class LoginScreen(val name: String = "Login") : Route()
    data class SignUpScreen(val name: String = "SignUp") : Route()
    data class PrivacyScreen(val name: String = "Privacy") : Route()
    data class HomeScreen(val name: String = "Home") : Route()
    data class LocationScreen(val name: String = "Location") : Route()
    data class ProfileScreen(val name: String = "Profile") : Route()
    data class EventsAndQuizzesScreen(val name: String = "EventsAndQuizzes") : Route()
    data class RankingScreen(val name: String = "RankingScreen") : Route()
    data class AllMarkerScreen(val name: String = "AllMarkerScreen") : Route()
    data class EditProfileScreen(val name: String = "EditProfileScreen") : Route()
}

@Composable
fun MyNavigation(navHostController: NavHostController) {
    NavHost(navController = navHostController, startDestination = Route.HomeScreen().name) {
        composable(route = Route.HomeScreen().name) {
            HomeScreen(
                navigateToLogin = {
                    navHostController.navigate(Route.LoginScreen().name)
                }
            )
        }

        navigation(startDestination = Route.LoginScreen().name, route = "login_flow") {
            composable(route = Route.LoginScreen().name) {
                LoginScreen(
                    onLoginClick = { username ->
                        navHostController.navigate("${Route.ProfileScreen().name}/$username") {
                            popUpTo(Route.HomeScreen().name) { inclusive = true }
                        }
                    },
                    onSignUpClick = {
                        navHostController.navigateToSingleTop(Route.SignUpScreen().name)
                    }
                )
            }
            composable(route = Route.SignUpScreen().name) {
                SignUpScreen(
                    onSignUpClick = {
                        navHostController.navigate(Route.LoginScreen().name) {
                            popUpTo(Route.HomeScreen().name) { inclusive = true }
                        }
                    },
                    onLoginClick = {
                        navHostController.navigateToSingleTop(Route.LoginScreen().name)
                    },
                    onPrivacyClick = {
                        navHostController.navigate(Route.PrivacyScreen().name) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(route = Route.PrivacyScreen().name) {
                PrivacyScreen {
                    navHostController.navigateUp()
                }
            }
        }

        /*composable(route = Route.LocationScreen().name) {
            LocationScreen(navController = navHostController)
        }*/


        composable(route = "${Route.LocationScreen().name}/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: return@composable
            LocationScreen(username = username, navController = navHostController)
        }

        composable(route = "${Route.ProfileScreen().name}/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: return@composable
            ProfileScreen(username = username, navController = navHostController)
        }

        composable(route = "${Route.EventsAndQuizzesScreen().name}/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: return@composable
            EventsAndQuizzesScreen(username = username, navController = navHostController)
        }

        /*composable(route = Route.RankingScreen().name) {
            RankingScreen(navController = navHostController)
        }*/

        composable(route = "RankingScreen/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            RankingScreen(navController = navHostController, username = username)
        }

        composable(route = "AllMarkerScreen/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            AllMarkerScreen(navController = navHostController, username = username)
        }


       /* composable(route = Route.AllMarkerScreen().name) {
            AllMarkerScreen(navController = navHostController)
        }*/

        composable(route = "${Route.EditProfileScreen().name}/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: return@composable
            EditProfileScreen(username = username, navController = navHostController)
        }
    }
}

fun NavController.navigateToSingleTop(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
