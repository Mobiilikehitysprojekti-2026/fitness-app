package com.example.fitnessapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fitnessapp.data.local.entity.UserAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAccount(user: UserAccount): Long  // add or update a user

    @Query("DELETE FROM user_accounts WHERE id = :id")
    suspend fun deleteUserAccount(id: String)   // delete a user by id

    @Query("SELECT * FROM user_accounts WHERE id = :id")
    suspend fun getUserAccount(id: String): UserAccount?

    @Query("SELECT * FROM user_accounts WHERE username = :username LIMIT 1")
    suspend fun getUserAccountByUsername(username: String): UserAccount?

    @Update
    suspend fun updateUserAccount(user: UserAccount) // update a user
}