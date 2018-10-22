package example.com.project306.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseService {
    private var mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    var mCurrentUser: MutableLiveData<FirebaseUser> = MutableLiveData()


    init {
        mCurrentUser.value = mAuth?.currentUser
    }

    fun attemptLogin(email: String, password: String): LiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        mAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener {
            if (it.isSuccessful) {
                mCurrentUser.value = mAuth?.currentUser
                result.value = ""
            } else {
                result.value = it.exception.toString()
            }
        }
        return result
    }

    fun attemptCreateAccount(email: String, password: String): LiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        mAuth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener {
            if (it.isSuccessful) {
                mCurrentUser.value = mAuth?.currentUser
                result.value = ""
            } else {
                result.value = it.exception.toString()
            }
        }
        return result
    }

    fun createUserWithEmailAndPassword(email: String, password: String): LiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        mAuth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener {
            if (it.isSuccessful) {
                mCurrentUser.value = mAuth?.currentUser
                result.value = ""
            } else {
                result.value = it.exception.toString()
            }
        }
        return result
    }

    fun sendEmailVerification(email: String): LiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        mCurrentUser.value?.sendEmailVerification()?.addOnCompleteListener {
            if (it.isSuccessful) {
                result.value = ""
            } else {
                result.value = it.exception.toString()
            }
        }
        return result
    }

    fun getUserDisplayName(): String? {
        return mCurrentUser.value?.displayName
    }

    fun firebaseLogout(): MutableLiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        mCurrentUser.value = null
        mAuth?.signOut()
        result.value = "success"
        return result
    }

    companion object {
        private val LOG_TAG: String = FirebaseService::class.java.name

        // For Singleton instantiation
        @Volatile
        private var instance: FirebaseService? = null

        fun getInstance() =
                instance ?: synchronized(this) {
                    instance ?: FirebaseService().also { instance = it }
                }
    }
}