package example.com.project306.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import example.com.project306.data.FirebaseService
import example.com.project306.data.MainRepository

class LoginViewModel(mainRepository: MainRepository) : ViewModel() {

    private var firebaseService: FirebaseService = mainRepository.getFirebaseService()

    fun attemptLogin(email: String, password: String): LiveData<String> {
        return firebaseService.attemptLogin(email, password)
    }
}