package example.com.project306.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import example.com.project306.data.MainRepository

class SignUpViewModel(private val mainRepository: MainRepository) : ViewModel() {
    fun attemptCreateAccount(email: String, password: String): LiveData<String> {
        return mainRepository.attemptCreateAccout(email, password)
    }

    fun attemptEmailVerification(): LiveData<String> {
        return mainRepository.attemptEmailVerification()
    }
}