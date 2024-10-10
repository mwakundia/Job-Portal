package com.example.alumnijobapp.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.alumnijobapp.nav.Screen
import com.example.alumnijobapp.utils.SharedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobListScreen(navController: NavController, sharedViewModel: SharedViewModel) {
    val jobs by sharedViewModel.jobs.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Job Listings") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Notifications.route) }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(jobs) { job ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    onClick = {
                        sharedViewModel.selectJob(job)
                        navController.navigate(Screen.JobDetail.route)
                    }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = job.title, style = MaterialTheme.typography.headlineSmall)
                        Text(text = job.company, style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Posted: ${job.postedDate}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}