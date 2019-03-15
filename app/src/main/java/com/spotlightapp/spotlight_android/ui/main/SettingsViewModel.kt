package com.spotlightapp.spotlight_android.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.spotlightapp.spotlight_android.data.MainRepository

class SettingsViewModel(private val mainRepository: MainRepository) : ViewModel() {
    var areUserValuesLoading: MutableLiveData<Boolean> = MutableLiveData()
    var isUserDataLoading: MutableLiveData<Boolean> = MutableLiveData()
    val valueOne: MutableLiveData<String> = MutableLiveData()
    val valueTwo: MutableLiveData<String> = MutableLiveData()
    val valueThree: MutableLiveData<String> = MutableLiveData()
    val displayName: MutableLiveData<String> = getDisplayNameFromRepository()
    val email: MutableLiveData<String> = getEmailFromRepository()
    var tempDisplayName: String = ""
    private var userDataSyncFlag: Boolean = false

    init {
        areUserValuesLoading.value = true
        isUserDataLoading.value = true
    }

    fun logout(): MutableLiveData<String> {
        return mainRepository.accountsLogout()
    }

    fun getUserValues(): MutableLiveData<ArrayList<String?>> {
        return mainRepository.getUserValues()
    }

    fun changeDisplayName(name: String): MutableLiveData<String> {
        return mainRepository.changeDisplayName(name)
    }

    fun reauthenticateUser(password: String): MutableLiveData<String> {
        return mainRepository.reauthenticateUser(password)
    }

    fun updatePassword(newPassword: String): MutableLiveData<String> {
        return mainRepository.updatePassword(newPassword)
    }

    fun setAppBarVisibility(makeVisible: Boolean) {
        mainRepository.isAppBarVisible.value = makeVisible
    }

    fun setBottomNavVisibility(makeVisible: Boolean) {
        mainRepository.isBottomNavVisible.value = makeVisible
    }

    /*
        Helper functions to synchronize progress bar with loading of data
     */
    private fun getDisplayNameFromRepository(): MutableLiveData<String> {
        val name = mainRepository.getDisplayName() // critical operation, could take a second
        if (!userDataSyncFlag) {
            userDataSyncFlag = true
        } else {
            isUserDataLoading.value = false
        }
        return name
    }

    private fun getEmailFromRepository(): MutableLiveData<String> {
        val e = mainRepository.getEmail()
        if (!userDataSyncFlag) {
            userDataSyncFlag = true
        } else {
            isUserDataLoading.value = false
        }
        return e
    }
}