package com.example.alumnijobapp.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.alumnijobapp.nav.Screen
import com.example.alumnijobapp.utils.SharedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(navController: NavController, sharedViewModel: SharedViewModel) {
    val currentUser = sharedViewModel.currentUser.collectAsState()
    var selectedItem by remember { mutableStateOf(0) }
    var showProfileMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Admin Dashboard") },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Notifications.route) }) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Admin Notifications",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Box {
                        IconButton(onClick = { showProfileMenu = true }) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "User Profile",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        DropdownMenu(
                            expanded = showProfileMenu,
                            onDismissRequest = { showProfileMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("View Profile") },
                                onClick = {
                                    showProfileMenu = false
                                    navController.navigate(Screen.Profile.route)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Logout") },
                                onClick = {
                                    showProfileMenu = false
                                    // Implement logout logic here
                                    // For example:
                                    // sharedViewModel.logout()
                                    navController.navigate(Screen.Welcome.route) {
                                        popUpTo(Screen.Welcome.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                val items = listOf("Dashboard", "Jobs", "Users", "Profile")
                val icons = listOf(Icons.Outlined.Dashboard, Icons.Outlined.Work, Icons.Outlined.Group, Icons.Outlined.Person)
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            when (index) {
                                1 -> navController.navigate(Screen.JobList.route)
                                2 -> navController.navigate(Screen.AdminUserManagement.route)
                                3 -> navController.navigate(Screen.Profile.route)
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WelcomeCard(currentUser.value?.name ?: "")
            Spacer(modifier = Modifier.height(16.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { DashboardCard(Icons.Default.Add, "Post New Job") { navController.navigate(Screen.AdminPostJob.route) } }
                item { DashboardCard(Icons.Default.List, "Manage Jobs") { navController.navigate(Screen.JobList.route) } }
                item { DashboardCard(Icons.Default.Group, "User Management") { navController.navigate(Screen.AdminUserManagement.route) } }
                item { DashboardCard(Icons.Default.Analytics, "Analytics") { /* Navigate to Analytics */ } }
                item { DashboardCard(Icons.Default.WorkHistory, "Applied Jobs") { navController.navigate(Screen.AdminAppliedJobs.route) } }
            }
        }
    }
}

@Composable
fun WelcomeCard(name: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        )
    ) {
        Text(
            text = "Welcome, Admin $name!",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Composable
fun DashboardCard(icon: ImageVector, title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = title,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                textAlign = TextAlign.Center
            )
        }
    }
}

// New screen for viewing applied jobs
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAppliedJobsScreen(navController: NavController, sharedViewModel: SharedViewModel) {
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
        // This is a placeholder for the actual implementation
        // You would typically fetch this data from your ViewModel or repository
        val appliedJobs = remember { mutableStateListOf(
            "Software Engineer" to "John Doe",
            "Data Analyst" to "Jane Smith",
            "Product Manager" to "Alice Johnson"
        ) }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            items(appliedJobs.size) { index ->
                val (job, applicant) = appliedJobs[index]
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
                            text = job,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Applied by: $applicant",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}