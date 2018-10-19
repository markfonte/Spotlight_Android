package example.com.project306.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseService {
    private var mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    private var mCurrentUser: FirebaseUser? = null

    init {
        mCurrentUser = mAuth?.currentUser
    }

    fun getCurrentUser(): LiveData<FirebaseUser> {
        val result: MutableLiveData<FirebaseUser> = MutableLiveData()
        result.value = mCurrentUser
        return result
    }

    fun attemptLogin(email: String, password: String): LiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        mAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener {
            if (it.isSuccessful) {
                mCurrentUser = mAuth?.currentUser
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
                mCurrentUser = mAuth?.currentUser
                result.value = ""
            } else {
                result.value = it.exception.toString()
            }

        }
        return result
    }

    fun getUserDisplayName(): String? {
        return mCurrentUser?.displayName
    }

    fun firebaseLogout() : MutableLiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
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