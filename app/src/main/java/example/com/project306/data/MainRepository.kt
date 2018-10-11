package example.com.project306.data

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseUser

class MainRepository {
    private var firebaseService: FirebaseService = FirebaseService.getInstance()

    fun attemptLogin(email: String, password: String): LiveData<String> {
        return firebaseService.attemptLogin(email, password)
    }

    fun getCurrentUser(): LiveData<FirebaseUser> {
        return firebaseService.getCurrentUser()
    }

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: MainRepository? = null

        fun getInstance() =
                instance ?: synchronized(this) {
                    instance ?: MainRepository().also { instance = it }
                }
    }
}