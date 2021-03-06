package com.spotlightapp.spotlight_android.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.spotlightapp.spotlight_android.data.MainRepository

class SignUpViewModel(private val mainRepository: MainRepository) : ViewModel() {
    val showConfirmPassword: MutableLiveData<Boolean> = MutableLiveData()
    val showCreateAccountButton: MutableLiveData<Boolean> = MutableLiveData()
    val isCreatingAccount: MutableLiveData<Boolean> = MutableLiveData()

    fun attemptCreateAccount(email: String, password: String, displayName: String): LiveData<String> {
        return mainRepository.attemptCreateAccount(email, password, displayName)
    }

    fun attemptEmailVerification(): LiveData<String> {
        return mainRepository.attemptEmailVerification()
    }

    init {
        showConfirmPassword.value = false
        showCreateAccountButton.value = false
        isCreatingAccount.value = false
    }
}