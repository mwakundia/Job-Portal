package com.example.alumnijobapp.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SharedViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _currentUser = MutableStateFlow<UserData?>(null)
    val currentUser: StateFlow<UserData?> = _currentUser

    private val _jobs = MutableStateFlow<List<Job>>(emptyList())
    val jobs: StateFlow<List<Job>> = _jobs

    private val _selectedJob = MutableStateFlow<Job?>(null)
    val selectedJob: StateFlow<Job?> = _selectedJob

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                fetchCurrentUser()
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun signup(name: String, email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val userId = authResult.user?.uid ?: throw Exception("User ID not found")

                val userData = UserData(userId, name, email, false, emptyList(), emptyList())
                firestore.collection("users").document(userId).set(userData).await()

                _currentUser.value = userData
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    private suspend fun fetchCurrentUser() {
        val userId = auth.currentUser?.uid ?: return
        val userDoc = firestore.collection("users").document(userId).get().await()
        _currentUser.value = userDoc.toObject(UserData::class.java)
    }

    fun logout() {
        auth.signOut()
        _currentUser.value = null
    }

    fun fetchJobs() {
        viewModelScope.launch {
            try {
                val querySnapshot = firestore.collection("jobs").get().await()
                _jobs.value = querySnapshot.toObjects(Job::class.java)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun selectJob(job: Job) {
        _selectedJob.value = job
    }

    fun applyForJob(jobId: String) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                firestore.collection("users").document(userId)
                    .update("appliedJobs", FieldValue.arrayUnion(jobId))
                    .await()
                fetchCurrentUser() // Refresh user data
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun postJob(job: Job) {
        viewModelScope.launch {
            try {
                firestore.collection("jobs").add(job).await()
                fetchJobs() // Refresh job list
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun fetchNotifications() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val querySnapshot = firestore.collection("notifications")
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()
                _notifications.value = querySnapshot.toObjects(Notification::class.java)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}