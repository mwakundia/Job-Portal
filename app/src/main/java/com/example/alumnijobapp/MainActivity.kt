package com.example.alumnijobapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alumnijobapp.nav.NavGraph
import com.example.alumnijobapp.ui.theme.AlumniJobAppTheme
import com.example.alumnijobapp.utils.NotificationUtils
import com.example.alumnijobapp.utils.SharedViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class MainActivity : ComponentActivity() {

    // Launcher for notification permission request
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            NotificationUtils.createNotificationChannel(this)
        } else {
            Toast.makeText(
                this,
                "Notification permission is required to receive job updates",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase with error handling
        try {
            FirebaseApp.initializeApp(this)  // Firebase initialization
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false) // Disable Firestore offline persistence
                .build()
            FirebaseFirestore.getInstance().firestoreSettings = settings
        } catch (e: Exception) {
            Log.e("MainActivity", "Error initializing Firebase: ${e.message}")
        }

        // Check for Google Play Services
        checkGooglePlayServices()

        // Set the UI content
        setContent {
            AlumniJobAppContent()
        }
    }

    // Check if Google Play Services is available
    private fun checkGooglePlayServices() {
        val availability = GoogleApiAvailability.getInstance()
        val resultCode = availability.isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (availability.isUserResolvableError(resultCode)) {
                availability.getErrorDialog(this, resultCode, 9000)?.show()
            } else {
                Log.e("MainActivity", "This device is not supported for Google Play Services.")
                Toast.makeText(this, "Google Play Services is not available on this device.", Toast.LENGTH_LONG).show()
                finish()
            }
        } else {
            initializeNotifications()
        }
    }

    // Main Composable content
    @Composable
    private fun AlumniJobAppContent() {
        AlumniJobAppTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                val sharedViewModel: SharedViewModel = viewModel()
                NavGraph(sharedViewModel = sharedViewModel)
            }
        }
    }

    // Initialize notification channel and request notification permissions
    private fun initializeNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {  // Android 13+
            when {
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                        PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                    NotificationUtils.createNotificationChannel(this)
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Show an explanation to the user why the app needs notification permission
                    Toast.makeText(
                        this,
                        "Notifications are needed to receive job updates",
                        Toast.LENGTH_LONG
                    ).show()
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    // Directly request the permission
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // For devices below Android 13, just create the notification channel
            NotificationUtils.createNotificationChannel(this)
        }
    }
}
