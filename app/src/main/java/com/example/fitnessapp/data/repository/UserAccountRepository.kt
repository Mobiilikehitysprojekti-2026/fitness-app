package com.example.fitnessapp.data.repository


import com.example.fitnessapp.data.local.dao.UserAccountDao
import com.example.fitnessapp.data.local.entity.UserAccount
import com.example.fitnessapp.managers.UserPreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first

/*
* UserAccountRepository contains the the current user account data and functions to modify a userAccount.
* All the ViewModels must have a userAccountRepository.
* */
class UserAccountRepository(
    private val userAccountDao: UserAccountDao,
    private val userPreferencesManager: UserPreferencesManager
    ) {

    // currentUserAccount exists across the app
    private val _currentUserAccount = MutableStateFlow<UserAccount?>(null)
    val currentUserAccount: StateFlow<UserAccount?> = _currentUserAccount


    // insert a userAccount
    suspend fun insertUserAccount(userAccount: UserAccount) {
        userAccountDao.insertUserAccount(userAccount)
    }


    // delete a userAccount by id
    suspend fun deleteUserAccount(id: String) {
        userAccountDao.deleteUserAccount(id)
    }


    ///
    // load the userAccount from the DB associated with the saved user id
    suspend fun loadSavedUserAccount() {
        val id = userPreferencesManager.userId.first() // Reads the ID once and completes
        if (id != null) {
            _currentUserAccount.value = userAccountDao.getUserAccount(id)
        }
    }

    // login   (call after login)
    // saves the userId in the storage and loads user data from the DB
    suspend fun setCurrentUserAccount(id: String) {
        userPreferencesManager.saveUserId(id)
        _currentUserAccount.value = userAccountDao.getUserAccount(id)
    }



    // logout
    suspend fun logout() {
        userPreferencesManager.clearUserId()
        _currentUserAccount.value = null
    }

    // login
    suspend fun login(username: String, password: String): Boolean {
        val userAccount = userAccountDao.login(username, password)

        return if (userAccount != null) {
            setCurrentUserAccount(userAccount.id)
            true
        } else {
            false
        }
    }

    suspend fun updateHeight(height: Int) {
        _currentUserAccount.value?.let { user ->
            val updatedUser = user.copy(height = height)
            userAccountDao.insertUserAccount(updatedUser)
            _currentUserAccount.value = updatedUser
        }
    }

    suspend fun updateWeight(weight: Int) {
        _currentUserAccount.value?.let { user ->
            val updatedUser = user.copy(weight = weight)
            userAccountDao.insertUserAccount(updatedUser)
            _currentUserAccount.value = updatedUser
        }
    }

}