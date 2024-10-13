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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SharedViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _currentUser = MutableStateFlow<UserData?>(null)
    val currentUser: StateFlow<UserData?> = _currentUser

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin

    private val _selectedJob = MutableStateFlow<Job?>(null)
    val selectedJob: StateFlow<Job?> = _selectedJob


    private val _jobs = MutableStateFlow<List<Job>>(emptyList())
    val jobs: StateFlow<List<Job>> = _jobs

    private val _jobApplications = MutableStateFlow<List<JobApplication>>(emptyList())
    val jobApplications: StateFlow<List<JobApplication>> = _jobApplications

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    private val _networkingEvents = MutableStateFlow<List<NetworkingEvent>>(emptyList())
    val networkingEvents: StateFlow<List<NetworkingEvent>> = _networkingEvents

    init {
        auth.currentUser?.let {
            viewModelScope.launch {
                fetchCurrentUser()
                fetchJobs()
                fetchJobApplications()
                fetchNotifications()
                fetchNetworkingEvents()
            }
        }
    }

    fun fetchJobApplications() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
                val querySnapshot = withContext(Dispatchers.IO) {
                    firestore.collection("jobApplications")
                        .whereEqualTo("applicantId", userId)
                        .get()
                        .await()
                }
                _jobApplications.value = querySnapshot.toObjects(JobApplication::class.java)
            } catch (e: Exception) {
                _error.value = "Failed to fetch job applications: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateApplicationStatus(applicationId: String, newStatus: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                withContext(Dispatchers.IO) {
                    firestore.collection("jobApplications").document(applicationId)
                        .update("status", newStatus)
                        .await()
                }
                fetchJobApplications()
            } catch (e: Exception) {
                _error.value = "Failed to update application status: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun login(email: String, password: String, onResult: (Result<UserRole>) -> Unit) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                withContext(Dispatchers.IO) {
                    auth.signInWithEmailAndPassword(email, password).await()
                }
                fetchCurrentUser()
                fetchJobs()
                fetchJobApplications()
                fetchNotifications()
                fetchNetworkingEvents()

                _currentUser.value?.let { user ->
                    onResult(Result.success(user.role))
                } ?: onResult(Result.failure(Exception("User data not found")))
            } catch (e: Exception) {
                _error.value = "Login failed: ${e.message}"
                onResult(Result.failure(e))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signup(
        name: String,
        email: String,
        password: String,
        role: UserRole,
        adminCode: String? = null,
        onResult: (Result<UserRole>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                if (role == UserRole.ADMIN && adminCode != "ADMIN123") {
                    throw Exception("Invalid admin code")
                }

                val authResult = withContext(Dispatchers.IO) {
                    auth.createUserWithEmailAndPassword(email, password).await()
                }
                val userId = authResult.user?.uid ?: throw Exception("User ID not found")

                val userData = UserData(
                    id = userId,
                    name = name,
                    email = email,
                    role = role
                )

                withContext(Dispatchers.IO) {
                    firestore.collection("users").document(userId).set(userData).await()
                }

                _currentUser.value = userData
                _isAdmin.value = (role == UserRole.ADMIN)
                fetchJobs()
                fetchJobApplications()
                fetchNotifications()
                fetchNetworkingEvents()
                onResult(Result.success(role))
            } catch (e: Exception) {
                _error.value = "Signup failed: ${e.message}"
                onResult(Result.failure(e))
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun fetchCurrentUser() {
        try {
            _isLoading.value = true
            _error.value = null
            val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
            val userDoc = withContext(Dispatchers.IO) {
                firestore.collection("users").document(userId).get().await()
            }
            _currentUser.value = userDoc.toObject(UserData::class.java)
            _isAdmin.value = (_currentUser.value?.role == UserRole.ADMIN)
        } catch (e: Exception) {
            _error.value = "Failed to fetch user data: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    fun logout() {
        auth.signOut()
        _currentUser.value = null
        _isAdmin.value = false
        _jobs.value = emptyList()
        _jobApplications.value = emptyList()
        _notifications.value = emptyList()
        _networkingEvents.value = emptyList()
        _error.value = null
    }

    fun fetchJobs() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val querySnapshot = withContext(Dispatchers.IO) {
                    if (_isAdmin.value) {
                        firestore.collection("jobs").get().await()
                    } else {
                        firestore.collection("jobs")
                            .whereEqualTo("isApproved", true)
                            .get()
                            .await()
                    }
                }
                _jobs.value = querySnapshot.toObjects(Job::class.java)
            } catch (e: Exception) {
                _error.value = "Failed to fetch jobs: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectJob(job: Job) {
        // Implementation for selecting a job
    }

    fun applyForJob(jobId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
                if (_currentUser.value?.role == UserRole.ALUMNI) {
                    withContext(Dispatchers.IO) {
                        firestore.collection("users").document(userId)
                            .update("appliedJobs", FieldValue.arrayUnion(jobId))
                            .await()
                    }
                    fetchCurrentUser()
                }
            } catch (e: Exception) {
                _error.value = "Failed to apply for job: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun postJob(job: Job) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                if (_isAdmin.value) {
                    withContext(Dispatchers.IO) {
                        firestore.collection("jobs").add(job).await()
                    }
                    fetchJobs()
                } else {
                    throw Exception("Only admins can post jobs")
                }
            } catch (e: Exception) {
                _error.value = "Failed to post job: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun approveJob(jobId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                if (_isAdmin.value) {
                    withContext(Dispatchers.IO) {
                        firestore.collection("jobs").document(jobId)
                            .update("isApproved", true)
                            .await()
                    }
                    fetchJobs()
                } else {
                    throw Exception("Only admins can approve jobs")
                }
            } catch (e: Exception) {
                _error.value = "Failed to approve job: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchNotifications() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
                val querySnapshot = withContext(Dispatchers.IO) {
                    firestore.collection("notifications")
                        .whereEqualTo("userId", userId)
                        .get()
                        .await()
                }
                _notifications.value = querySnapshot.toObjects(Notification::class.java)
            } catch (e: Exception) {
                _error.value = "Failed to fetch notifications: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchNetworkingEvents() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val querySnapshot = withContext(Dispatchers.IO) {
                    firestore.collection("networkingEvents").get().await()
                }
                _networkingEvents.value = querySnapshot.toObjects(NetworkingEvent::class.java)
            } catch (e: Exception) {
                _error.value = "Failed to fetch networking events: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createNetworkingEvent(event: NetworkingEvent) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                if (_isAdmin.value) {
                    withContext(Dispatchers.IO) {
                        firestore.collection("networkingEvents").add(event).await()
                    }
                    fetchNetworkingEvents()
                } else {
                    throw Exception("Only admins can create networking events")
                }
            } catch (e: Exception) {
                _error.value = "Failed to create networking event: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getUserAnalytics(): UserAnalytics {
        // Implement user analytics logic here
        return UserAnalytics()
    }

    fun getJobAnalytics(): JobAnalytics {
        if (!_isAdmin.value) {
            return JobAnalytics() // Return empty analytics for non-admins
        }
        // Implement job analytics logic here
        return JobAnalytics()
    }
}