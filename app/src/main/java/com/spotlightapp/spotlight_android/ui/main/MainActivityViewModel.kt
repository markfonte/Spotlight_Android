package com.spotlightapp.spotlight_android.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.spotlightapp.spotlight_android.data.MainRepository

class MainActivityViewModel(private val mainRepository: MainRepository) : ViewModel() {
    var isBottomNavVisible: MutableLiveData<Boolean> = mainRepository.isBottomNavVisible
    var isAppBarVisible: MutableLiveData<Boolean> = mainRepository.isAppBarVisible
    var currentUser: MutableLiveData<FirebaseUser> = mainRepository.getCurrentUser()

    fun setAppBarVisibility(makeVisible: Boolean) {
        mainRepository.isAppBarVisible.value = makeVisible
    }

    fun setBottomNavVisibility(makeVisible: Boolean) {
        mainRepository.isBottomNavVisible.value = makeVisible
    }
}