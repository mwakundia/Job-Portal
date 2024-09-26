package com.example.alumnijobapp.utils

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class UserData(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val isAdmin: Boolean = false,
    val skills: List<String> = emptyList(),
    val appliedJobs: List<String> = emptyList()
)

data class Job(
    val id: String = "",
    val title: String = "",
    val company: String = "",
    val description: String = "",
    val requirements: String = "",
    @ServerTimestamp
    val postedDate: Date? = null,
    val expirationDate: Date? = null
)

data class Notification(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    @ServerTimestamp
    val timestamp: Date? = null
)