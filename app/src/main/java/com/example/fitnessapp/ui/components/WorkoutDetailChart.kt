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
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries

@Composable
fun WorkoutDetailChart(
    paceData: List<Float>,
    modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(paceData) {
        if (paceData.isNotEmpty()) {
            modelProducer.runTransaction {
                lineSeries {
                    series(paceData)
                }
            }
        }
    }

    // Ensure formatters never return an empty string
    val startAxisValueFormatter = remember {
        CartesianValueFormatter { _, value, _ -> 
            val formatted = "%.1f".format(value)
            formatted.ifEmpty { value.toString() }
        }
    }
    
    val bottomAxisValueFormatter = remember {
        CartesianValueFormatter { _, value, _ ->
            // Display minute (1-based index)
            (value.toInt() + 1).toString()
        }
    }

    val axisLabelComponent = rememberTextComponent(
        color = MaterialTheme.colorScheme.onSurface
    )

    Column(modifier = modifier) {
        Text(
            text = "Pace per Minute (min/km)",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        if (paceData.isEmpty()) {
            Text(
                text = "No pace data available for this workout.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(),
                    startAxis = VerticalAxis.rememberStart(
                        label = axisLabelComponent,
                        valueFormatter = startAxisValueFormatter,
                        itemPlacer = remember { VerticalAxis.ItemPlacer.step() }
                    ),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        label = axisLabelComponent,
                        valueFormatter = bottomAxisValueFormatter,
                        itemPlacer = remember { HorizontalAxis.ItemPlacer.aligned(spacing = { 1 }) }
                    ),
                ),
                modelProducer = modelProducer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
            )
        }
    }
}
