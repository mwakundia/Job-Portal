package com.example.alumnijobapp.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.Alignment
import com.example.alumnijobapp.nav.Screen
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.alumnijobapp.utils.SharedViewModel
import com.example.alumnijobapp.utils.Job

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageJobsScreen(navController: NavController, sharedViewModel: SharedViewModel) {
    val jobs by sharedViewModel.jobs.collectAsState() // Observe jobs from ViewModel
    val isLoading by sharedViewModel.isLoading.collectAsState() // Observe loading state

    LaunchedEffect(Unit) {
        // Fetch jobs when the screen is displayed
        sharedViewModel.fetchJobs()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Jobs") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.AdminPostJob.route) }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Job")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                items(jobs) { job ->
                    JobCard(job, onEdit = {
                        navController.navigate(Screen.AdminEditJob.route + "/${job.id}")
                    }, onDelete = {
                        // Delete the job and update the UI
                        sharedViewModel.deleteJob(job.id)
                    })
                }
            }

        }
    }
}

@Composable
fun JobCard(job: Job, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = job.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = job.company,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onEdit) {
                    Text("Edit")
                }
                Button(onClick = onDelete, colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )) {
                    Text("Delete")
                }
            }
        }
    }
}