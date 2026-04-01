package com.example.fitnessapp.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

/*
* UserAccount is an entity class for the database.
* There can several users in the database.
* */
@Entity(
    tableName = "user_accounts",
    indices = [Index(value = ["username"], unique = true)] // username must be unique
)
data class UserAccount(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val username: String,
    val password: String,
    val name: String,
    val age: Int,
    val height: Int,
    val weight: Int
)
