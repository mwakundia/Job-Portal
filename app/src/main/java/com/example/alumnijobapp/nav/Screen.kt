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
    object AdminAppliedJobs : Screen("admin_applied_jobs")
    object Analytics : Screen("analytics")


    object Network : Screen("network")
    object AdminEditJob : Screen("admin_edit_job/{jobId}")

    object ForgotPassword : Screen("forgot_password")
    object ResetPassword : Screen("reset_password")
    object AdminJobManagement : Screen("admin_job_management")
    object AdminUserManagement : Screen("admin_user_management")
    object SearchJobs : Screen("search_jobs")
    object SavedJobs : Screen("saved_jobs")
    object ApplicationStatus : Screen("application_status")
    object EditProfile : Screen("edit_profile")
    object Settings : Screen("settings")
    object Help : Screen("help")
    object AdminDashboard : Screen("admin_dashboard")
    object AlumniDashboard : Screen("alumni_dashboard")
}