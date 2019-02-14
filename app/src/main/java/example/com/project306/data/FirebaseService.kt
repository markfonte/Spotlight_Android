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
import example.com.project306.util.CrashlyticsHelper.Companion.logDebugTask
import example.com.project306.util.CrashlyticsHelper.Companion.logErrorTask
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
        mAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                logDebugTask(task, logTag = LOG_TAG, functionName = "attemptLogin()", message = "login credentials valid.")
                if (!mAuth?.currentUser?.isEmailVerified!!) {
                    logDebugTask(task, logTag = LOG_TAG, functionName = "attemptLogin()", message = "email not verified.")
                    result.value = "email not verified"
                } else {
                    logDebugTask(task, logTag = LOG_TAG, functionName = "attemptLogin()", message = "email verified, login successful.")
                    mCurrentUser.value = mAuth?.currentUser
                    result.value = ""
                }
            } else {
                logErrorTask(task, logTag = LOG_TAG, functionName = "attemptLogin()", message = "error signing in user.")
                result.value = task.exception.toString()
            }
        }
        return result
    }

    fun attemptCreateAccount(email: String, password: String, displayName: String): LiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        mAuth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener { task ->
            if (task.isSuccessful) { //do not sign them in yet
                logDebugTask(task, logTag = LOG_TAG, functionName = "attemptCreateAccount()", message = "account successfully created. account setup required.")
                setupNewAccount(displayName)
                result.value = ""
            } else {
                logErrorTask(task, logTag = LOG_TAG, functionName = "attemptCreateAccount()", message = "error creating account.")
                result.value = task.exception.toString()
            }
        }
        return result
    }

    /**
     *  createAccountWithEmailAndPassword automatically logs user in. While this is not the end
     *  functionality we want, we can leverage this to do the first-time login work - such as
     *  creating the user-specific document in the database and setting "are_values_set" to false
     *  so we know to start the on-boarding process when they first log in. Then this function logs
     *  them out and we continue with our expected functionality.
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
                logDebugTask(task, logTag = LOG_TAG, functionName = "changeDisplayName()", message = "display name successfully changed.")
                result.value = ""
            } else {
                logErrorTask(task, logTag = LOG_TAG, functionName = "changeDisplayName()", message = "error changing user display name.")
                result.value = task.exception.toString()
            }
        }
        return result
    }

    fun sendEmailVerification(): LiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        mAuth?.currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                logDebugTask(task, logTag = LOG_TAG, functionName = "sendEmailVerification()", message = "email verification sent.")
                result.value = ""
            } else {
                logErrorTask(task, logTag = LOG_TAG, functionName = "sendEmailVerification()", message = "error sending email verification.")
                result.value = task.exception.toString()
            }
        }
        return result
    }

    fun firebaseLogout(): MutableLiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        mCurrentUser.value = null
        mAuth?.signOut()
        result.value = ""
        return result
    }

    fun sendPasswordResetEmail(email: String): MutableLiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        mAuth?.sendPasswordResetEmail(email)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                logDebugTask(task, logTag = LOG_TAG, functionName = "sendPasswordResetEmail()", message = "password reset email sent.")
                result.value = ""
            } else {
                logErrorTask(task, logTag = LOG_TAG, functionName = "sendPasswordResetEmail()", message = "error sending password reset email.")
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
                logDebugTask(task, logTag = LOG_TAG, functionName = "reauthenticateUser()", message = "user reauthenticated.")
                result.value = ""
            } else {
                logErrorTask(task, logTag = LOG_TAG, functionName = "reauthenticateUser()", message = "error reauthenticating user.")
                result.value = task.exception.toString()
            }
        }
        return result
    }

    fun updatePassword(newPassword: String): MutableLiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        mAuth?.currentUser?.updatePassword(newPassword)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                logDebugTask(task, logTag = LOG_TAG, functionName = "updatePassword()", message = "password successfully updated.")
                result.value = ""
            } else {
                logErrorTask(task, logTag = LOG_TAG, functionName = "updatePassword()", message = "error updating user password")
                result.value = task.exception.toString()
            }
        }
        return result
    }

    /**
     * Firestore functions
     */

    fun overwriteUserInformation(values: MutableMap<String, Any>): LiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        fsDb.collection("users").document(mAuth?.currentUser?.uid!!).set(values)
                .addOnSuccessListener {
                    Log.d(LOG_TAG, "Successfully overwrote user document")
                    result.value = ""
                }
                .addOnFailureListener { exception ->
                    Log.e(LOG_TAG, "Error writing to document", exception)
                    result.value = exception.toString()
                }
        return result
    }


    @Suppress("UNCHECKED_CAST")
    fun getUserValues(): MutableLiveData<ArrayList<String?>> {
        val result: MutableLiveData<ArrayList<String?>> = MutableLiveData()
        fsDb.collection("users").document(mAuth?.currentUser?.uid!!).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                logDebugTask(task, logTag = LOG_TAG, functionName = "getUserValues()", message = "successfully retrieved user document for user values.")
                result.value = task.result?.data?.get("values") as? ArrayList<String?>

            } else {
                logErrorTask(task, logTag = LOG_TAG, functionName = "getUserValues()", message = "error retrieving user document for user values.")
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

    @Suppress("UNCHECKED_CAST")
    fun getSchedule(): MutableLiveData<HashMap<String, HashMap<String, String>>> {
        val result: MutableLiveData<HashMap<String, HashMap<String, String>>> = MutableLiveData()
        fsDb.collection("users").document(mAuth?.currentUser?.uid!!).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document: DocumentSnapshot = task.result!!
                if (document.exists()) {
                    val userDocument = document.data
                    if (userDocument?.get("current_schedule") != null) {
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

    @Suppress("UNCHECKED_CAST")
    fun getStaticHouseData(): MutableLiveData<HashMap<String, HashMap<String, String>>> {
        val result: MutableLiveData<HashMap<String, HashMap<String, String>>> = MutableLiveData()
        fsDb.collection("panhel_data").document("house_information").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document: DocumentSnapshot = task.result!!
                if (document.exists()) {
                    result.value = document.data as? HashMap<String, HashMap<String, String>>
                }
            } else {
                Log.e(LOG_TAG, "getStaticHouseData task failed", task.exception)
                result.value = null
            }
        }
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

    @Suppress("UNCHECKED_CAST")
    fun getCurrentRanking(): MutableLiveData<HashMap<String, Int>> {
        val result: MutableLiveData<HashMap<String, Int>> = MutableLiveData()
        fsDb.collection("users").document(mAuth?.currentUser?.uid!!).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                logDebugTask(task, logTag = LOG_TAG, functionName = "getCurrentRanking()", message = "successfully retrieved user document for current ranking.")
                result.value = task.result?.data?.get("current_ranking") as? HashMap<String, Int>
            } else {
                logErrorTask(task, logTag = LOG_TAG, functionName = "getCurrentRanking()", message = "error retrieving user document for current ranking.")
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
                logDebugTask(task, logTag = LOG_TAG, functionName = "updateNote()", message = "successfully updated note $houseId")
                result.value = ""
            } else {
                logErrorTask(task, logTag = LOG_TAG, functionName = "updateNote()", message = "error updating note $houseId")

                result.value = task.exception.toString()
            }
        }
        return result
    }

    @Suppress("UNCHECKED_CAST")
    fun getNote(houseId: String): MutableLiveData<HashMap<String, Any>> {
        val result: MutableLiveData<HashMap<String, Any>> = MutableLiveData()
        fsDb.collection("users").document(mAuth?.currentUser?.uid!!).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                logDebugTask(task, logTag = LOG_TAG, functionName = "getNote()", message = "successfully retrieved note $houseId")
                val notes = task.result?.data?.get("notes") as? HashMap<String, HashMap<String, Any>>
                result.value = notes?.get(houseId)
            } else {
                logErrorTask(task, logTag = LOG_TAG, functionName = "getNote()", message = "error retrieving note at $houseId")
                result.value = null
            }
        }
        return result
    }

    fun updateRanking(updatedRanking: HashMap<String, Int>): MutableLiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        val rankingUpdatesMap = HashMap<String, Any>()
        for ((rankingKey, rankingValue) in updatedRanking) {
            rankingUpdatesMap["current_ranking.$rankingKey"] = rankingValue
        }
        fsDb.collection("users").document(mAuth?.currentUser?.uid!!).update(rankingUpdatesMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                logDebugTask(task, logTag = LOG_TAG, functionName = "updateRanking()", message = "successfully updated current_ranking.")
                result.value = ""
            } else {
                logErrorTask(task, logTag = LOG_TAG, functionName = "updateRanking()", message = "error updating current_ranking.")
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