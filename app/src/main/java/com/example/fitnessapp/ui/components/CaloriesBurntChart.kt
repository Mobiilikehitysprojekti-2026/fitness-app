package com.example.fitnessapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun CaloriesBurntChart(
    caloriesData: List<Int>,
    modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(caloriesData) {
        if (caloriesData.isNotEmpty()) {
            modelProducer.runTransaction {
                lineSeries {
                    series(caloriesData)
                }
            }
        }
    }

    val daysOfWeek = remember {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("EE", Locale.getDefault())
        val days = mutableListOf<String>()
        for (i in 6 downTo 0) {
            val tempCalendar = calendar.clone() as Calendar
            tempCalendar.add(Calendar.DAY_OF_YEAR, -i)
            days.add(sdf.format(tempCalendar.time))
        }
        days
    }

    val bottomAxisValueFormatter = remember(daysOfWeek) {
        CartesianValueFormatter { _, value, _ ->
            daysOfWeek.getOrElse(value.toInt()) { "" }
        }
    }

    Column(modifier = modifier) {
        Text(
            text = "Calories Burnt (Last 7 Days)",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        if (caloriesData.isEmpty() || caloriesData.all { it == 0 }) {
            Text(
                text = "No workout data for the last 7 days.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(),
                    startAxis = VerticalAxis.rememberStart(
                        itemPlacer = remember { VerticalAxis.ItemPlacer.step() }
                    ),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        valueFormatter = bottomAxisValueFormatter,
                        itemPlacer = remember { HorizontalAxis.ItemPlacer.aligned(spacing = { 1 }) }
                    ),
                ),
                modelProducer = modelProducer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
            )
        }
    }
}
