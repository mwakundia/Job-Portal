package com.example.alumnijobapp.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.alumnijobapp.screen.*
import com.example.alumnijobapp.utils.SharedViewModel
import com.google.android.libraries.intelligence.acceleration.Analytics

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    sharedViewModel: SharedViewModel
) {
    NavHost(navController = navController, startDestination = Screen.Welcome.route) {
        composable(Screen.Welcome.route) { WelcomeScreen(navController) }
        composable(Screen.Login.route + "/{role}") { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role")
            LoginScreen(navController, sharedViewModel, redirectRole = role)
        }
        composable(Screen.Signup.route) { SignupScreen(navController, sharedViewModel) }
        composable(Screen.JobList.route) { JobListScreen(navController, sharedViewModel) }
        composable(Screen.JobDetail.route) { JobDetailScreen(navController, sharedViewModel) }
        composable(Screen.JobApplication.route) { JobApplicationScreen(navController, sharedViewModel) }
        composable(Screen.Profile.route) { ProfileScreen(navController, sharedViewModel) }
        composable(Screen.AdminPostJob.route) { AdminPostJobScreen(navController, sharedViewModel) }
        composable(Screen.Notifications.route) { NotificationsScreen(navController, sharedViewModel) }
        composable(Screen.AdminDashboard.route) { AdminDashboardScreen(navController, sharedViewModel) }
        composable(Screen.AlumniDashboard.route) { AlumniDashboardScreen(navController, sharedViewModel) }
        composable(Screen.AdminAppliedJobs.route) { AdminAppliedJobsScreen(navController, sharedViewModel) }

        // Inside your NavHost composable
        composable(Screen.JobList.route) { ManageJobsScreen(navController, sharedViewModel) }
        composable(Screen.AdminUserManagement.route) { UserManagementScreen(navController, sharedViewModel) }
        composable(Screen.Analytics.route) { AnalyticsScreen(navController,sharedViewModel) }
//        composable(Screen.EditJob.route) { EditJobScreen(navController, sharedViewModel) }
//        composable(Screen.EditUser.route) { EditUserScreen(navController, sharedViewModel) }

    }
}