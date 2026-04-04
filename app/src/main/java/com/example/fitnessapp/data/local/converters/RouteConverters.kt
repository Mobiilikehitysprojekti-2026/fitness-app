package com.example.fitnessapp.data.local.converters

import androidx.room.TypeConverter
import com.example.fitnessapp.data.model.Coordinates
import kotlinx.serialization.json.Json

// convert from/to json
class RouteConverters {
    @TypeConverter
    fun fromCoordinatesList(value: List<Coordinates>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toCoordinatesList(value: String): List<Coordinates> {
        return Json.decodeFromString(value)
    }
}
