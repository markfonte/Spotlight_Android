package example.com.project306.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import example.com.project306.data.FirebaseService
import example.com.project306.data.MainRepository

class LoginViewModel(private val mainRepository: MainRepository) : ViewModel() {

    var firebaseService : FirebaseService = mainRepository.getFirebaseService()
    fun attemptLogin(email: String, password: String) : LiveData<FirebaseUser> {
        return firebaseService.attemptLogin(email, password)
    }
}