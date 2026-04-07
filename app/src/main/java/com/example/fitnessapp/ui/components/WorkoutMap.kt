package com.example.fitnessapp.ui.components

import android.graphics.DashPathEffect
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.fitnessapp.data.model.Coordinates
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.util.BoundingBox
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.Marker

@Composable
fun WorkoutMap(
    modifier: Modifier = Modifier,
    routePoints: List<Coordinates>,
    currentLocation: Coordinates? = null,
    isStatic: Boolean = false // New parameter to distinguish history view from live tracking
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    // Cleanup when the composable is destroyed
    DisposableEffect(Unit) {
        onDispose {
            mapView.onDetach()
        }
    }

    AndroidView(
        factory = {
            mapView.apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(17.0)
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp)),
        update = { view ->
            // Очищаем старые наложения (линии и маркеры)
            view.overlays.clear()

            // Route (Polyline)
            if (routePoints.size >= 2) {
                val polyline = Polyline().apply {
                    outlinePaint.color = android.graphics.Color.RED
                    outlinePaint.strokeWidth = 8f
                    // Dotted line
                    outlinePaint.pathEffect = DashPathEffect(floatArrayOf(20f, 10f), 0f)
                    setPoints(routePoints.map { GeoPoint(it.latitude, it.longitude) })
                }
                view.overlays.add(polyline)
            }

            // Marker on current position
            currentLocation?.let {
                val currentPoint = GeoPoint(it.latitude, it.longitude)
                val marker = Marker(view).apply {
                    position = currentPoint
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    
                    val starDrawable = ContextCompat.getDrawable(context, android.R.drawable.btn_star_big_on)
                    this.icon = starDrawable
                    
                    title = "Location"
                }
                view.overlays.add(marker)
                
                // If it's live tracking, follow the user
                if (!isStatic) {
                    view.controller.animateTo(currentPoint)
                }
            }

            // If it's a saved workout (history), zoom to fit the entire route
            if (isStatic && routePoints.isNotEmpty()) {
                view.post { // Wait for view to be laid out
                    if (routePoints.size >= 2) {
                        val boundingBox = BoundingBox.fromGeoPoints(routePoints.map { GeoPoint(it.latitude, it.longitude) })
                        // Add some padding to the bounding box
                        view.zoomToBoundingBox(boundingBox, false, 100)
                    } else if (routePoints.size == 1) {
                        view.controller.setCenter(GeoPoint(routePoints[0].latitude, routePoints[0].longitude))
                        view.controller.setZoom(17.0)
                    }
                }
            }

            view.invalidate()
        }
    )
}
