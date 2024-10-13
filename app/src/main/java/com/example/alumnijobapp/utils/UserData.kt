package com.example.alumnijobapp.utils

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class UserData(
    val id: String = "",
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
    val title: String = "",
    val company: String = "",
    val description: String = "",
    val location: String = "",
    val salary: String = "",
    val requirements: List<String> = emptyList(),
    @ServerTimestamp
    val postedDate: Date? = null,
    val postedBy: String = "",
    val type: String = "",
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

data class UserAnalytics(
    val totalUsers: Int = 0,
    val activeUsers: Int = 0,
    val newUsersThisMonth: Int = 0
)

data class JobAnalytics(
    val totalJobs: Int = 0,
    val activeJobs: Int = 0,
    val applicationsThisMonth: Int = 0
)
data class JobApplication(
    val id: String,
    val jobId: String,
    val jobTitle: String,
    val applicantId: String,
    val applicantName: String,
    val applicationDate: String,
    val status: String
)