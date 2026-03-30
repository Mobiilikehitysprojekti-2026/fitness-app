package com.example.fitnessapp.data.repository


import com.example.fitnessapp.data.local.dao.UserAccountDao
import com.example.fitnessapp.data.local.entity.UserAccount
import kotlinx.coroutines.flow.Flow

/*
* UserAccountRepository contains the the current user account data and functions to modify a userAccount.
* All the ViewModels must have a useAccountRepository.
* */
class UserAccountRepository(private val userAccountDao: UserAccountDao) {

    // currentUserAccount exists across the app
    val currentUserAccount: Flow<UserAccount?> = userAccountDao.getUserAccount(1) // currently we have only 1 user


    // insert a userAccount
    suspend fun insertUserAccount(userAccount: UserAccount) {
        userAccountDao.insertUserAccount(userAccount)
    }


    // delete a userAccount by id
    suspend fun deleteUserAccount(id: Int) {
        userAccountDao.deleteUserAccount(id)
    }
}