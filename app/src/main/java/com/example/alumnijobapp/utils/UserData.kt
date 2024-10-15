package com.example.alumnijobapp.utils

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class UserData(
    val id: String = "",
    val firstName: String,
    val lastName: String,
    val isActive: Boolean,
    val name: String = "",
    val email: String = "",
    val isAdmin: Boolean = false,
    val role: UserRole = UserRole.ALUMNI,
    val skills: List<String> = emptyList(),
    val appliedJobs: List<String> = emptyList(),
    val savedJobs: List<String> = emptyList(),
    @ServerTimestamp
    val createdAt: Date? = null,
    val profileImageUrl: String = "",
    val bio: String = "",
    val experience: List<Experience> = emptyList()
)

data class Job(
    val id: String = "",
    val title: String = "", // renamed to 'title' to match the parameter in postJob function
    val company: String = "", // renamed to 'company' to match the parameter in postJob function
    val description: String = "",
    val location: String = "",
    val salary: String = "",
    val requirements: List<String> = emptyList(),
    val isApproved: Boolean = false,
    @ServerTimestamp val createdAt: Date? = null,
    val postedDate: Date? = null,
    val postedBy: String = "",
    val type: String = "", // renamed to 'type' to match the parameter in postJob function
    val applicants: List<String> = emptyList(),
    val status: String = "active",
    val category: String = "",
    val applicationDeadline: Date? = null
)

data class Notification(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    @ServerTimestamp
    val timestamp: Date? = null,
    val isRead: Boolean = false,
    val type: String = "",
    val relatedJobId: String = "",
    val actionUrl: String = ""
)

data class Experience(
    val id: String = "",
    val company: String = "",
    val position: String = "",
    val startDate: Date? = null,
    val endDate: Date? = null,
    val description: String = "",
    val isCurrentPosition: Boolean = false
)
data class NetworkingEvent(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: Long = 0,
    val location: String = ""
)


data class AnalyticsScreenData(
    val totalUsers: Int,
    val activeJobs: Int,
    val totalApplications: Int,
    val avgApplicationsPerJob: Double,
    val monthlyJobPostings: List<Pair<String, Int>>
)
data class JobApplication(
    val id: String,
    val jobId: String,
    val jobTitle: String,
    val applicantId: String,
    val applicantName: String,
    val applicationDate: Date? = null,
    val status: String
)