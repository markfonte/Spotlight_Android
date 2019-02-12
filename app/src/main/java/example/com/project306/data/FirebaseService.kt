package example.com.project306.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import kotlin.collections.HashMap

class FirebaseService {
    private var mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    private var fsDb: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var storage = FirebaseStorage.getInstance()

    var mCurrentUser: MutableLiveData<FirebaseUser> = MutableLiveData()

    init {
        mCurrentUser.value = mAuth?.currentUser
    }

    /**
     *  Auth functions
     */
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
        //newUserMap["currently_unranked"] =
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

    fun firebaseLogout(): MutableLiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        mCurrentUser.value = null
        mAuth?.signOut()
        result.value = "success"
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

    /**
     * Firestore functions
     */

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

    fun updateUserInformation(values: MutableMap<String, Any>): MutableLiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        fsDb.collection("users").document(mAuth?.currentUser?.uid!!).update(values)
                .addOnSuccessListener {
                    Log.d(LOG_TAG, "Successfully updated user document")
                    result.value = ""
                }
                .addOnFailureListener {
                    Log.e(LOG_TAG, "Error updating user document", it)
                    result.value = it.toString()
                }
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

    fun getSchedule(): MutableLiveData<HashMap<String, HashMap<String, String>>> {
        val result: MutableLiveData<HashMap<String, HashMap<String, String>>> = MutableLiveData()
        fsDb.collection("users").document(mAuth?.currentUser?.uid!!).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document: DocumentSnapshot = task.result!!
                if (document.exists()) {
                    val userDocument = document.data
                    if (userDocument?.get("current_schedule") != null) {
                        @Suppress("UNCHECKED_CAST")
                        result.value = userDocument["current_schedule"] as? HashMap<String, HashMap<String, String>>
                    } else {
                        result.value = hashMapOf()
                    }
                }
            } else {
                Log.e(LOG_TAG, "getCurrentSchedule task failed", task.exception)
                result.value = hashMapOf()
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

    fun sandboxFunction(): MutableLiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
//        val updatesMap: MutableMap<String, Any> = HashMap()
//        updatesMap["fake_array.1"] = "dummy_temp_value"
//        fsDb.collection("users").document(mAuth?.currentUser?.uid!!).update(updatesMap)
//        fsDb.collection("users").document(mAuth?.currentUser?.uid!!).update("fake_array", FieldValue.arrayRemove("fake_value"))
//                .addOnSuccessListener {
//                    result.value = ""
//                }
//                .addOnFailureListener {
//                    result.value = it.toString()
//                }
        return result
    }

    fun submitNotes(houseIndex: String, houseId: String, comments: String, valueOne: Boolean, valueTwo: Boolean, valueThree: Boolean): MutableLiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        val saveNoteUpdatesMap = HashMap<String, Any>()
        saveNoteUpdatesMap["notes.$houseId.comments"] = comments
        saveNoteUpdatesMap["notes.$houseId.value1"] = valueOne
        saveNoteUpdatesMap["notes.$houseId.value2"] = valueTwo
        saveNoteUpdatesMap["notes.$houseId.value3"] = valueThree
        val userDoc = fsDb.collection("users").document(mAuth?.currentUser?.uid!!)
        userDoc.update(saveNoteUpdatesMap).addOnCompleteListener { task1 ->
            if (task1.isSuccessful) {
                val rankingUpdatesMap = HashMap<String, Any>()
                rankingUpdatesMap["current_ranking.$houseId"] = -1
                userDoc.update(rankingUpdatesMap).addOnCompleteListener { task2 ->
                    if (task2.isSuccessful) {
                        val removeIndexUpdatesMap = HashMap<String, Any>()
                        removeIndexUpdatesMap["current_schedule.$houseIndex"] = FieldValue.delete()
                        userDoc.update(removeIndexUpdatesMap).addOnCompleteListener { task3 ->
                            if (task3.isSuccessful) {
                                Log.d(LOG_TAG, "Successfully submitted note.")
                                result.value = ""
                            } else {
                                Log.e(LOG_TAG, task3.exception.toString(), task3.exception)
                                result.value = task3.exception.toString()
                            }
                        }
                    } else {
                        Log.e(LOG_TAG, task2.exception.toString(), task2.exception)
                        result.value = task2.exception.toString()
                    }
                }
            } else {
                Log.e(LOG_TAG, task1.exception.toString(), task1.exception)
                result.value = task1.exception.toString()
            }
        }
        return result
    }

    fun getCurrentRanking(): MutableLiveData<HashMap<String, Int>> {
        val result: MutableLiveData<HashMap<String, Int>> = MutableLiveData()
        fsDb.collection("users").document(mAuth?.currentUser?.uid!!).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(LOG_TAG, "Get current ranking task successful. ${task.result?.data}")
                @Suppress("UNCHECKED_CAST")
                result.value = task.result?.data?.get("current_ranking") as? HashMap<String, Int>
            } else {
                Log.e(LOG_TAG, task.exception.toString(), task.exception)
                result.value = null
            }

        }
        return result
    }

    fun updateNote(houseId: String, comments: String, valueOne: Boolean, valueTwo: Boolean, valueThree: Boolean): MutableLiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        val saveNoteUpdatesMap = HashMap<String, Any>()
        saveNoteUpdatesMap["notes.$houseId.comments"] = comments
        saveNoteUpdatesMap["notes.$houseId.value1"] = valueOne
        saveNoteUpdatesMap["notes.$houseId.value2"] = valueTwo
        saveNoteUpdatesMap["notes.$houseId.value3"] = valueThree
        fsDb.collection("users").document(mAuth?.currentUser?.uid!!).update(saveNoteUpdatesMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                result.value = ""
            } else {
                Log.e(LOG_TAG, task.exception.toString(), task.exception)
                result.value = task.exception.toString()
            }
        }
        return result
    }

    fun getNote(houseId: String): MutableLiveData<HashMap<String, Any>> {
        val result: MutableLiveData<HashMap<String, Any>> = MutableLiveData()
        fsDb.collection("users").document(mAuth?.currentUser?.uid!!).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(LOG_TAG, "Get note task successful. ${task.result?.data}")
                @Suppress("UNCHECKED_CAST")
                val notes = task.result?.data?.get("notes") as? HashMap<String, HashMap<String, Any>>
                result.value = notes?.get(houseId)
            } else {
                Log.e(LOG_TAG, task.exception.toString(), task.exception)
                result.value = null
            }
        }
        return result
    }

    fun updateRanking(updatedRanking: HashMap<String, Int>) : MutableLiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        val rankingUpdatesMap = HashMap<String, Any>()
        for((rankingKey, rankingValue) in updatedRanking) {
            rankingUpdatesMap["current_ranking.$rankingKey"] = rankingValue
        }
        fsDb.collection("users").document(mAuth?.currentUser?.uid!!).update(rankingUpdatesMap).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                result.value = ""
            }
            else {
                Log.e(LOG_TAG, task.exception.toString(), task.exception)
                result.value = task.exception.toString()
            }
        }
        return result
    }

    /**
     *   Storage functions
     */

    fun getStaticHouseImageReference(fileName: String): StorageReference {
        return storage.reference.child("house_images").child("$fileName.jpg")
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