package example.com.project306.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import example.com.project306.data.MainRepository

class LoginViewModel(private val mainRepository: MainRepository) : ViewModel() {
    fun attemptLogin(email: String, password: String): LiveData<String> {
        return mainRepository.attemptLogin(email, password)
    }

    fun attemptEmailVerification(email: String): LiveData<String> {
        return mainRepository.attemptEmailVerification(email)
    }
}