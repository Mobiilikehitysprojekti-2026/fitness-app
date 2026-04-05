package com.example.fitnessapp.data.local.converters

import androidx.room.TypeConverter
import com.example.fitnessapp.data.model.Coordinates
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// convert from/to json
class RouteConverters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromCoordinatesList(value: List<Coordinates>): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun toCoordinatesList(value: String): List<Coordinates> {
        return json.decodeFromString(value)
    }

    @TypeConverter
    fun fromFloatList(value: List<Float>): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun toFloatList(value: String): List<Float> {
        return json.decodeFromString(value)
    }
}
