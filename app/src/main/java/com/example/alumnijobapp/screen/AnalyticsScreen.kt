package com.example.alumnijobapp.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.alumnijobapp.utils.SharedViewModel
import com.patrykandpatryk.vico.core.entry.FloatEntry
import com.patrykandpatryk.vico.compose.chart.Chart
import com.patrykandpatryk.vico.compose.chart.line.lineChart
import com.patrykandpatryk.vico.core.entry.entryModelOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(navController: NavController, sharedViewModel: SharedViewModel) {
    var analyticsData by remember { mutableStateOf<AnalyticsData?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        // Fetch analytics data from the ViewModel or repository
        analyticsData = sharedViewModel.getAnalyticsData()
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            analyticsData?.let { data ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    item { AnalyticCard("Total Users", data.totalUsers.toString(), Icons.Default.Group) }
                    item { AnalyticCard("Active Jobs", data.activeJobs.toString(), Icons.Default.Work) }
                    item { AnalyticCard("Total Applications", data.totalApplications.toString(), Icons.Default.Description) }
                    item { AnalyticCard("Avg. Applications per Job", String.format("%.2f", data.avgApplicationsPerJob), Icons.Default.Analytics) }
                    item {
                        Text(
                            "Monthly Job Postings",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                        LineChart(data = data.monthlyJobPostings)
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticCard(title: String, value: String, icon: ImageVector) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun LineChart(data: List<Pair<String, Int>>) {
    val chartEntryModel = entryModelOf(data.mapIndexed { index, (_, value) ->
        FloatEntry(x = index.toFloat(), y = value.toFloat())
    })

    Chart(
        chart = lineChart(),
        model = chartEntryModel,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}

// Ensure this data class is in the appropriate package
data class AnalyticsData(
    val totalUsers: Int,
    val activeJobs: Int,
    val totalApplications: Int,
    val avgApplicationsPerJob: Double,
    val monthlyJobPostings: List<Pair<String, Int>>
)

// Add this function to SharedViewModel (if not already there)
suspend fun SharedViewModel.getAnalyticsData(): AnalyticsData {
    // Implement the logic to fetch analytics data
    // This is a placeholder implementation
    return AnalyticsData(
        totalUsers = 1000,
        activeJobs = 50,
        totalApplications = 500,
        avgApplicationsPerJob = 10.0,
        monthlyJobPostings = listOf(
            "Jan" to 10,
            "Feb" to 15,
            "Mar" to 20,
            "Apr" to 18,
            "May" to 25,
            "Jun" to 30
        )
    )
}
