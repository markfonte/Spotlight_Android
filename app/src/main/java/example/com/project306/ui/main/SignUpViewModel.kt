package example.com.project306.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import example.com.project306.data.MainRepository

class SignUpViewModel(private val mainRepository: MainRepository) : ViewModel() {
    fun attemptCreateAccount(email: String, password: String, displayName: String): LiveData<String> {
        return mainRepository.attemptCreateAccout(email, password, displayName)
    }

    fun attemptEmailVerification(): LiveData<String> {
        return mainRepository.attemptEmailVerification()
    }

    val showConfirmPassword: MutableLiveData<Boolean> = MutableLiveData()
    val showCreateAccountButton: MutableLiveData<Boolean> = MutableLiveData()

    init {
        showConfirmPassword.value = false
        showCreateAccountButton.value = false
    }
}