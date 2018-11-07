package example.com.project306.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser

class MainRepository {
    private var firebaseService: FirebaseService = FirebaseService.getInstance()
    var isBottomNavVisible: MutableLiveData<Boolean> = MutableLiveData()
    var isAppBarVisible: MutableLiveData<Boolean> = MutableLiveData()

    fun attemptLogin(email: String, password: String): LiveData<String> {
        return firebaseService.attemptLogin(email, password)
    }

    fun getCurrentUser(): MutableLiveData<FirebaseUser> {
        return firebaseService.mCurrentUser
    }

    fun accountsLogout(): MutableLiveData<String> {
        isBottomNavVisible.value = false
        isAppBarVisible.value = false
        return firebaseService.firebaseLogout()
    }

    fun attemptCreateAccout(email: String, password: String) :LiveData<String> {
        return firebaseService.attemptCreateAccount(email, password)
    }

    fun attemptEmailVerification(email: String): LiveData<String> {
        return firebaseService.sendEmailVerification(email)
    }

    fun getPanhelValues() : LiveData<ArrayList<*>> {
        return firebaseService.getPanhelValues()
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