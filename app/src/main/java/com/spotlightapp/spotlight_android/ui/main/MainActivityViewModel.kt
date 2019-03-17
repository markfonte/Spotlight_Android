package com.spotlightapp.spotlight_android.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.spotlightapp.spotlight_android.data.MainRepository
import com.spotlightapp.spotlight_android.util.CrashlyticsHelper
import com.spotlightapp.spotlight_android.util.UserState

class MainActivityViewModel(private val mainRepository: MainRepository) : ViewModel() {
    var isBottomNavVisible: MutableLiveData<Boolean> = mainRepository.isBottomNavVisible
    var isAppBarVisible: MutableLiveData<Boolean> = mainRepository.isAppBarVisible

    fun validateUser(): MutableLiveData<UserState> = mainRepository.validateUser()

    fun setAppBarVisibility(makeVisible: Boolean) {
        mainRepository.isAppBarVisible.value = makeVisible
    }

    fun setBottomNavVisibility(makeVisible: Boolean) {
        mainRepository.isBottomNavVisible.value = makeVisible
    }

    fun logoutWithError(): MutableLiveData<String> {
        CrashlyticsHelper.logError(exception = null, logTag = MainActivityViewModel::class.java.name, functionName = "logoutWithError()", message = "logging out with error. look into this immediately.")
        return mainRepository.accountsLogout()
    }
}