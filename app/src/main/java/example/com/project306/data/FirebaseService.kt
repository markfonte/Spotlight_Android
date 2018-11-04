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
                if(mAuth?.currentUser != null) {
                    var x = "do something"
                    x += "fuck"
                }
                setupNewAccount()
            } else {
                result.value = it.exception.toString()
            }
        }
        return result
    }

    /* createAccountWithEmailAndPassword automatically logs user in. While this
     is not the end functionality we want, we can leverage this to do the first-time
     login work - such as creating the user-specific document in the database and
     setting "areValuesSet" to "false" so we know to start the onboarding process
     when they first log in. Then this function logs them out and we continue
     with our expected functionality.
     */
    private fun setupNewAccount() : LiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        val newUserMap: MutableMap<String, Any> = HashMap()
        newUserMap["areValuesSet"] = "false"
        updateUserInformation(newUserMap)
        firebaseLogout()
        return result
    }

    fun sendEmailVerification(email: String): LiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        mAuth?.currentUser?.sendEmailVerification()?.addOnCompleteListener {
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

    private fun updateUserInformation(values: MutableMap<String, Any>): LiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        fsDb.collection("users").document(mAuth?.currentUser?.uid!!).set(values)
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