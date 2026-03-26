package com.example.fitnessapp.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/*
* UserAccount is an entity class for the database.
* There is (currently) only one user in the database.
* */
@Entity(
    tableName = "user_accounts",
    indices = [Index(value = ["username"], unique = true)] // username must be unique
)
data class UserAccount(
    @PrimaryKey val id: Int = 1,
    val username: String,
    val name: String,
    val age: Int,
    val height: Int,
    val weight: Int
)
