package com.example.fitnessapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessapp.data.local.entity.WorkoutSession
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries

@Composable
fun WorkoutsByTypeChart(sessions: List<WorkoutSession>) {
    val workoutsByType = remember(sessions) {
        sessions.groupBy { it.type }
            .map { (type, list) -> 
                val displayType = if (type.isBlank()) "Other" else type.replaceFirstChar { it.uppercase() }
                displayType to list.size 
            }
            .sortedBy { it.first }
    }

    ChartCard(
        title = "Workouts by Type",
        data = workoutsByType,
        noDataText = "No type data available"
    )
}

@Composable
private fun ChartCard(
    title: String,
    data: List<Pair<String, Int>>,
    noDataText: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (data.isNotEmpty()) {
                val modelProducer = remember { CartesianChartModelProducer() }

                LaunchedEffect(data) {
                    modelProducer.runTransaction {
                        columnSeries {
                            series(data.map { it.second.toFloat() })
                        }
                    }
                }

                CartesianChartHost(
                    chart = rememberCartesianChart(
                        rememberColumnCartesianLayer(),
                        startAxis = VerticalAxis.rememberStart(),
                        bottomAxis = HorizontalAxis.rememberBottom(
                            valueFormatter = { _, value, _ ->
                                data.getOrNull(value.toInt())?.first ?: "N/A"
                            },
                            itemPlacer = HorizontalAxis.ItemPlacer.aligned()
                        ),
                    ),
                    modelProducer = modelProducer,
                    modifier = Modifier.height(200.dp)
                )
            } else {
                Text(
                    text = noDataText,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        }
    }
}
