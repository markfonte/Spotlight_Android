package com.spotlightapp.spotlight_android.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.spotlightapp.spotlight_android.data.MainRepository

class LoginViewModel(private val mainRepository: MainRepository) : ViewModel() {
    fun attemptLogin(email: String, password: String): LiveData<String> {
        return mainRepository.attemptLogin(email, password)
    }

    fun attemptEmailVerification(): LiveData<String> {
        return mainRepository.attemptEmailVerification()
    }

    fun sendPasswordResetEmail(email: String): MutableLiveData<String> {
        return mainRepository.sendPasswordResetEmail(email)
    }

    val showLoginButton: MutableLiveData<Boolean> = MutableLiveData()

    init {
        showLoginButton.value = false
    }
}