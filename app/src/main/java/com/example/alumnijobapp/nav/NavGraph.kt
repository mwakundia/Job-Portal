package com.example.alumnijobapp.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.alumnijobapp.screen.*
import com.example.alumnijobapp.utils.SharedViewModel

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    sharedViewModel: SharedViewModel
) {
    NavHost(navController = navController, startDestination = Screen.Welcome.route) {
        composable(Screen.Welcome.route) { WelcomeScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController, sharedViewModel) }
        composable(Screen.Signup.route) { SignupScreen(navController, sharedViewModel) }
        composable(Screen.JobList.route) { JobListScreen(navController, sharedViewModel) }
        composable(Screen.JobDetail.route) { JobDetailScreen(navController, sharedViewModel) }
        composable(Screen.JobApplication.route) { JobApplicationScreen(navController, sharedViewModel) }
        composable(Screen.Profile.route) { ProfileScreen(navController, sharedViewModel) }
        composable(Screen.AdminPostJob.route) { AdminPostJobScreen(navController, sharedViewModel) }
        composable(Screen.Notifications.route) { NotificationsScreen(navController, sharedViewModel) }
        composable(Screen.AdminDashboard.route) { AdminDashboardScreen(navController, sharedViewModel) }
        composable(Screen.AlumniDashboard.route) { AlumniDashboardScreen(navController, sharedViewModel) }
    }
}