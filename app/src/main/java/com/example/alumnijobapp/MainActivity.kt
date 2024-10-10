package com.example.alumnijobapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
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
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
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

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Initialize notification channel and request permissions
        initializeNotifications()

        setContent {
            AlumniJobAppContent()
        }
    }

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

    private fun initializeNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                        PackageManager.PERMISSION_GRANTED -> {
                    NotificationUtils.createNotificationChannel(this)
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    Toast.makeText(
                        this,
                        "Notifications are needed to receive job updates",
                        Toast.LENGTH_LONG
                    ).show()
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            NotificationUtils.createNotificationChannel(this)
        }
    }
}