package com.example.fitnessapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitnessapp.data.local.entity.UserAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAccount(user: UserAccount): Long  // add or update a user

    @Query("DELETE FROM user_accounts WHERE id = :id")
    suspend fun deleteUserAccount(id: Int)   // delete a user by id

    @Query("SELECT * FROM user_accounts WHERE id = :id")
    fun getUserAccount(id: Int): Flow<UserAccount?>
}