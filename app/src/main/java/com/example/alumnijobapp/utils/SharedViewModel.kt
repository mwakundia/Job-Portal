package com.example.alumnijobapp.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

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

    suspend fun getAllUsers(): List<UserData> = withContext(Dispatchers.IO) {
        try {
            val querySnapshot = firestore.collection("users").get().await()
            return@withContext querySnapshot.toObjects(UserData::class.java)
        } catch (e: Exception) {
            _error.value = "Failed to fetch users: ${e.message}"
            return@withContext emptyList()
        }
    }

    suspend fun toggleUserActivation(userId: String) = withContext(Dispatchers.IO) {
        try {
            val userRef = firestore.collection("users").document(userId)
            val userSnapshot = userRef.get().await()
            val currentActiveStatus = userSnapshot.getBoolean("isActive") ?: true
            userRef.update("isActive", !currentActiveStatus).await()
        } catch (e: Exception) {
            _error.value = "Failed to toggle user activation: ${e.message}"
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

    fun deleteJob(jobId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                withContext(Dispatchers.IO) {
                    firestore.collection("jobs").document(jobId).delete().await()
                }
                fetchJobs()
            } catch (e: Exception) {
                _error.value = "Failed to delete job: ${e.message}"
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
                val userData = fetchCurrentUser()
                if (userData != null) {
                    fetchJobs()
                    fetchJobApplications()
                    fetchNotifications()
                    fetchNetworkingEvents()
                    onResult(Result.success(userData.role))
                } else {
                    onResult(Result.failure(Exception("User data not found")))
                }
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
                    firstName = name.split(" ").firstOrNull() ?: "",
                    lastName = name.split(" ").lastOrNull() ?: "",
                    isActive = true,
                    name = name,
                    email = email,
                    isAdmin = (role == UserRole.ADMIN),
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

    private suspend fun fetchCurrentUser(): UserData? {
        return try {
            _isLoading.value = true
            _error.value = null
            val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
            val userDoc = withContext(Dispatchers.IO) {
                firestore.collection("users").document(userId).get().await()
            }
            val userData = userDoc.toObject(UserData::class.java)
            _currentUser.value = userData
            _isAdmin.value = (userData?.role == UserRole.ADMIN)
            userData
        } catch (e: Exception) {
            _error.value = "Failed to fetch user data: ${e.message}"
            null
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

    fun applyForJob(jobId: String, jobTitle: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
                val jobApplication = JobApplication(
                    id = firestore.collection("jobApplications").document().id,
                    jobId = jobId,
                    jobTitle = jobTitle,
                    applicantId = userId,
                    applicantName = _currentUser.value?.name ?: "",
                    status = "Pending",
                    applicationDate = Date()
                )
                withContext(Dispatchers.IO) {
                    firestore.collection("jobApplications").document(jobApplication.id).set(jobApplication).await()
                }
                fetchJobApplications()
            } catch (e: Exception) {
                _error.value = "Failed to apply for job: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun postJob(
        title: String,
        company: String,
        location: String,
        salary: String,
        type: String,
        description: String
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
                val newJob = Job(
                    id = firestore.collection("jobs").document().id,
                    title = title,
                    company = company,
                    description = description,
                    location = location,
                    salary = salary,
                    type = type,
                    postedBy = userId,
                    postedDate = Date(),
                    isApproved = _isAdmin.value
                )
                withContext(Dispatchers.IO) {
                    firestore.collection("jobs").document(newJob.id).set(newJob).await()
                }
                fetchJobs()
            } catch (e: Exception) {
                _error.value = "Failed to post job: ${e.message}"
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

    fun selectJob(job: Job?) {
        _selectedJob.value = job
    }
}