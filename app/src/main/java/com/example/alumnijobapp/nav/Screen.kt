package com.example.alumnijobapp.nav

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Signup : Screen("signup")
    object JobList : Screen("job_list")
    object JobDetail : Screen("job_detail")
    object JobApplication : Screen("job_application")
    object Profile : Screen("profile")
    object AdminPostJob : Screen("admin_post_job")
    object Notifications : Screen("notifications")
    object Dashboard : Screen("dashboard")
}