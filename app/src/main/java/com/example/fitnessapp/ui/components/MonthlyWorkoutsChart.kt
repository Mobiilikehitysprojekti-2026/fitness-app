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
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import java.util.Calendar

@Composable
fun MonthlyWorkoutsChart(sessions: List<WorkoutSession>) {
    val monthNames = remember { arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec") }

    val monthlyWorkouts = remember(sessions) {
        val counts = mutableMapOf<String, Int>()
        val now = Calendar.getInstance()
        
        for (i in 0 until 12) {
            val tempCal = Calendar.getInstance()
            tempCal.time = now.time
            tempCal.add(Calendar.MONTH, -i)
            val year = tempCal.get(Calendar.YEAR)
            val month = tempCal.get(Calendar.MONTH)
            val key = "%d-%02d".format(year, month)
            counts[key] = 0
        }

        sessions.forEach { session ->
            val cal = Calendar.getInstance()
            cal.timeInMillis = session.startTime
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val key = "%d-%02d".format(year, month)
            if (counts.containsKey(key)) {
                counts[key] = (counts[key] ?: 0) + 1
            }
        }

        counts.keys.sorted().map { key ->
            val parts = key.split("-")
            val yearShort = parts[0].takeLast(2)
            val monthIdx = parts[1].toInt()
            "${monthNames[monthIdx]} '$yearShort" to (counts[key] ?: 0)
        }
    }

    ChartCard(
        title = "Monthly Workouts",
        data = monthlyWorkouts,
        noDataText = "No monthly data available"
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

                val axisLabelComponent = rememberTextComponent(
                    color = MaterialTheme.colorScheme.onSurface
                )

                CartesianChartHost(
                    chart = rememberCartesianChart(
                        rememberColumnCartesianLayer(),
                        startAxis = VerticalAxis.rememberStart(
                            label = axisLabelComponent
                        ),
                        bottomAxis = HorizontalAxis.rememberBottom(
                            label = axisLabelComponent,
                            valueFormatter = { _, value, _ ->
                                data.getOrNull(value.toInt())?.first ?: value.toString()
                            }
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
