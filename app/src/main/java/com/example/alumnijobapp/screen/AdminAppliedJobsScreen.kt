package com.example.alumnijobapp.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.alumnijobapp.utils.SharedViewModel
import com.example.alumnijobapp.utils.JobApplication
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAppliedJobsScreenContent(navController: NavController, sharedViewModel: SharedViewModel) {
    val applications by sharedViewModel.jobApplications.collectAsState()
    val isLoading by sharedViewModel.isLoading.collectAsState()
    val error by sharedViewModel.error.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        sharedViewModel.fetchJobApplications()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Applied Jobs") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (error != null) {
                Text(
                    text = "Error: $error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(applications) { application ->
                        ApplicationCard(
                            application = application,
                            onAccept = {
                                scope.launch {
                                    sharedViewModel.updateApplicationStatus(application.id, "Accepted")
                                }
                            },
                            onDeny = {
                                scope.launch {
                                    sharedViewModel.updateApplicationStatus(application.id, "Denied")
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ApplicationCard(
    application: JobApplication,
    onAccept: () -> Unit,
    onDeny: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                text = application.jobTitle, // Accessing job title from application
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Applicant: ${application.applicantId}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Applied on: ${application.applicationDate}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Status: ${application.status}",
                style = MaterialTheme.typography.bodyMedium,
                color = when (application.status) {
                    "Pending" -> MaterialTheme.colorScheme.primary
                    "Accepted" -> MaterialTheme.colorScheme.tertiary
                    "Denied" -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onAccept,
                    enabled = application.status == "Pending",
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    )
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Accept")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Accept")
                }
                Button(
                    onClick = onDeny,
                    enabled = application.status == "Pending",
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Deny")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Deny")
                }
            }
        }
    }
}
