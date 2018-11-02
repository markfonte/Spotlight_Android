package example.com.project306.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseService {
    private var mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    private var fsDb: FirebaseFirestore = FirebaseFirestore.getInstance()

    var mCurrentUser: MutableLiveData<FirebaseUser> = MutableLiveData()

    init {
        mCurrentUser.value = mAuth?.currentUser
    }

    fun attemptLogin(email: String, password: String): LiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        mAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener {
            if (it.isSuccessful) {
                if (!mAuth?.currentUser?.isEmailVerified!!) {
                    result.value = "email not verified"
                    firebaseLogout()
                } else {
                    mCurrentUser.value = mAuth?.currentUser
                    result.value = ""
//                    var dummyMap: MutableMap<String, Any> = HashMap()
//                    dummyMap.put("name", "Mark")
//                    updateUserInformation(dummyMap)
                }
            } else {
                result.value = it.exception.toString()
            }
        }
        return result
    }

    fun attemptCreateAccount(email: String, password: String): LiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        mAuth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener {
            if (it.isSuccessful) { //do not sign them in yet
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

    fun updateUserInformation(values: MutableMap<String, Any>): LiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        fsDb.collection("users").document(mCurrentUser.value?.uid!!).set(values)
                .addOnSuccessListener {
                    Log.d(LOG_TAG, "Successfully updated database")
                    result.value = ""
                }
                .addOnFailureListener {
                    Log.e(LOG_TAG, "Error writing to document", it)
                    result.value = it.toString()
                }
        return result
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