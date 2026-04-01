package com.example.fitnessapp.data.model

import kotlinx.serialization.Serializable

/*
* Coordinates class stores latitude and longitude of a specific GPS location
* */
@Serializable
data class Coordinates(val latitude: Double, val longitude: Double)