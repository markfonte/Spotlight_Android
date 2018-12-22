package example.com.project306.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class FirebaseService {
    private var mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    private var fsDb: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var storage = FirebaseStorage.getInstance()

    var mCurrentUser: MutableLiveData<FirebaseUser> = MutableLiveData()

    init {
        mCurrentUser.value = mAuth?.currentUser
    }

    fun getDisplayName(): MutableLiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        result.value = mAuth?.currentUser?.displayName
        return result
    }

    fun getEmail(): MutableLiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        result.value = mAuth?.currentUser?.email
        return result
    }


    @Suppress("UNCHECKED_CAST")
    fun getUserValues(): MutableLiveData<ArrayList<String?>> {
        val result: MutableLiveData<ArrayList<String?>> = MutableLiveData()
        fsDb.collection("users").document(mAuth?.currentUser?.uid!!).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document: DocumentSnapshot = task.result!!
                if (document.exists()) {
                    val userData = document.data

                    result.value = userData?.get("values") as? ArrayList<String?>
                }
            } else {
                Log.e(LOG_TAG, "task failed", task.exception)
                result.value = arrayListOf()
            }

        }
        return result
    }

    fun attemptLogin(email: String, password: String): LiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        mAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener {
            if (it.isSuccessful) {
                if (!mAuth?.currentUser?.isEmailVerified!!) {
                    result.value = "email not verified"
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

    fun attemptCreateAccount(email: String, password: String, displayName: String): LiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        mAuth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener {
            if (it.isSuccessful) { //do not sign them in yet
                setupNewAccount(displayName)
                result.value = ""
            } else {
                result.value = it.exception.toString()
            }
        }
        return result
    }

    /* createAccountWithEmailAndPassword automatically logs user in. While this
     is not the end functionality we want, we can leverage this to do the first-time
     login work - such as creating the user-specific document in the database and
     setting "are_values_set" to false so we know to start the on-boarding process
     when they first log in. Then this function logs them out and we continue
     with our expected functionality.
     */
    private fun setupNewAccount(displayName: String) {
        val newUserMap: MutableMap<String, Any> = HashMap()
        newUserMap["are_values_set"] = false
        newUserMap["bid_house"] = ""
        newUserMap["current_round"] = 0
        overwriteUserInformation(newUserMap)
        changeDisplayName(displayName)
    }

    fun changeDisplayName(name: String): MutableLiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
        mAuth?.currentUser?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(LOG_TAG, "Display name set")
                result.value = ""
            } else {
                Log.e(LOG_TAG, task.exception.toString())
                result.value = task.exception.toString()
            }
        }
        return result
    }

    fun sendEmailVerification(): LiveData<String> {
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

    fun areValuesSet(): LiveData<Boolean> {
        val result: MutableLiveData<Boolean> = MutableLiveData()
        fsDb.collection("users").document(mAuth?.currentUser?.uid!!).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document: DocumentSnapshot = task.result!!
                if (document.exists()) {
                    val userData = document.data
                    result.value = userData?.get("are_values_set") != false
                }
            }
        }
        return result
    }

    fun getScheduleData(): MutableLiveData<Triple<Long?, Boolean, String?>> {
        val result: MutableLiveData<Triple<Long?, Boolean, String?>> = MutableLiveData()
        fsDb.collection("users").document(mAuth?.currentUser?.uid!!).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document: DocumentSnapshot = task.result!!
                if (document.exists()) {
                    val userData = document.data
                    result.value = Triple(userData?.get("current_round") as? Long?, userData?.get("current_schedule") != null, userData?.get("bid_house") as? String?)
                } else {
                    Log.e(LOG_TAG, "Error retrieving current round.", task.exception)
                    result.value = Triple(-1, false, "")
                }
            }
        }
        return result
    }

    fun overwriteUserInformation(values: MutableMap<String, Any>): LiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        fsDb.collection("users").document(mAuth?.currentUser?.uid!!).set(values)
                .addOnSuccessListener {
                    Log.d(LOG_TAG, "Successfully overwrote user document")
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

    fun getPanhelValues(): LiveData<ArrayList<*>> {
        val result: MutableLiveData<ArrayList<*>> = MutableLiveData()
        fsDb.collection("panhel_data").document("panhel_values").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document: DocumentSnapshot = task.result!!
                if (document.exists()) {
                    val panhelValues = document.data
                    result.value = panhelValues?.get("values") as? ArrayList<*>
                }
            } else {
                Log.e(LOG_TAG, "task failed", task.exception)
                result.value = arrayListOf<String>()
            }
        }
        return result
    }

    fun getSchedule(scheduleName: String): LiveData<ArrayList<HashMap<String, String>>> {
        val result: MutableLiveData<ArrayList<HashMap<String, String>>> = MutableLiveData()
        fsDb.collection("users").document(mAuth?.currentUser?.uid!!).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document: DocumentSnapshot = task.result!!
                if (document.exists()) {
                    val userDocument = document.data
                    if (userDocument?.get(scheduleName) != null) {
                        @Suppress("UNCHECKED_CAST")
                        result.value = userDocument[scheduleName] as? ArrayList<HashMap<String, String>>
                    } else {
                        result.value = arrayListOf()
                    }
                }
            } else {
                Log.e(LOG_TAG, "getSchedule task failed", task.exception)
                result.value = arrayListOf()
            }
        }
        return result
    }

    fun getStaticHouseData(): MutableLiveData<HashMap<String, HashMap<String, String>>> {
        val result: MutableLiveData<HashMap<String, HashMap<String, String>>> = MutableLiveData()
        fsDb.collection("panhel_data").document("house_information").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document: DocumentSnapshot = task.result!!
                if (document.exists()) {
                    @Suppress("UNCHECKED_CAST")
                    result.value = document.data as? HashMap<String, HashMap<String, String>>
                }
            } else {
                Log.e(LOG_TAG, "getStaticHouseData task failed", task.exception)
                result.value = null
            }
        }
        return result
    }

    fun sendForgotPasswordEmail(email: String): MutableLiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        mAuth?.sendPasswordResetEmail(email)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                result.value = ""
            } else {
                Log.e(LOG_TAG, task.exception.toString(), task.exception)
                result.value = task.exception.toString()
            }
        }
        return result
    }

    fun reauthenticateUser(password: String): MutableLiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        val credential: AuthCredential = EmailAuthProvider.getCredential(mAuth?.currentUser?.email!!, password)
        mAuth?.currentUser?.reauthenticate(credential)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                result.value = ""
            } else {
                Log.e(LOG_TAG, task.exception.toString(), task.exception)
                result.value = task.exception.toString()
            }
        }
        return result
    }

    fun updatePassword(newPassword: String): MutableLiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        mAuth?.currentUser?.updatePassword(newPassword)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                result.value = ""
            } else {
                Log.e(LOG_TAG, task.exception.toString(), task.exception)
                result.value = task.exception.toString()
            }
        }
        return result
    }

    fun getStaticHouseImageReference(fileName: String): StorageReference {
        return storage.reference.child("house_images").child("$fileName.jpg")
    }

    fun performSubmitCommentsAction(): MutableLiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
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