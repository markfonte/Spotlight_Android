package com.spotlightapp.spotlight_android.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.spotlightapp.spotlight_android.util.CrashlyticsHelper.Companion.logDebug
import com.spotlightapp.spotlight_android.util.CrashlyticsHelper.Companion.logError
import com.spotlightapp.spotlight_android.util.CrashlyticsHelper.Companion.logErrorSnapshot
import com.spotlightapp.spotlight_android.util.CrashlyticsHelper.Companion.logErrorTask
import com.spotlightapp.spotlight_android.util.CrashlyticsHelper.Companion.resetCrashlyticsUserIdentifier
import com.spotlightapp.spotlight_android.util.CrashlyticsHelper.Companion.setCrashlyticsUserIdentifier
import com.spotlightapp.spotlight_android.util.DC
import com.spotlightapp.spotlight_android.util.UserState
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FirebaseService {
    private var mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    private var fsDb: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var storage = FirebaseStorage.getInstance()

    init {
        if (mAuth?.currentUser != null) {
            setCrashlyticsUserIdentifier(mAuth?.currentUser?.uid)
        } else {
            resetCrashlyticsUserIdentifier()
        }
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
                logDebug(logTag = LOG_TAG, functionName = "attemptLogin()", message = "login credentials valid with $email.")
                if (!mAuth?.currentUser?.isEmailVerified!!) {
                    logDebug(logTag = LOG_TAG, functionName = "attemptLogin()", message = "email $email not verified .")
                    result.value = "email not verified"
                } else {
                    logDebug(logTag = LOG_TAG, functionName = "attemptLogin()", message = "email $email verified, login successful.")
                    if (mAuth?.currentUser != null) {
                        setCrashlyticsUserIdentifier(mAuth?.currentUser?.uid)
                    }
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
            if (task.isSuccessful) {
                logDebug(logTag = LOG_TAG, functionName = "attemptCreateAccount()", message = "successfully created user account with $email. setup required.")
                setupNewAccount(displayName)
                result.value = ""
            } else {
                logErrorTask(task, logTag = LOG_TAG, functionName = "attemptCreateAccount()", message = "error creating account with $email.")
                result.value = task.exception.toString()
            }
        }
        return result
    }

    fun attemptFirebaseLogout(): MutableLiveData<String> {
        logDebug(logTag = LOG_TAG, functionName = "attemptFirebaseLogout()", message = "logging out user.")
        val result: MutableLiveData<String> = MutableLiveData()
        mAuth?.signOut()
        resetCrashlyticsUserIdentifier()
        result.value = ""
        return result
    }

    fun reauthenticateUser(password: String): MutableLiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        val credential: AuthCredential = EmailAuthProvider.getCredential(mAuth?.currentUser?.email!!, password)
        mAuth?.currentUser?.reauthenticate(credential)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                logDebug(logTag = LOG_TAG, functionName = "reauthenticateUser()", message = "successfully re-authenticated user.")
                result.value = ""
            } else {
                logErrorTask(task, logTag = LOG_TAG, functionName = "reauthenticateUser()", message = "error re-authenticating user.")
                result.value = task.exception.toString()
            }
        }
        return result
    }

    fun updateDisplayName(name: String): MutableLiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
        mAuth?.currentUser?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                logDebug(logTag = LOG_TAG, functionName = "updateDisplayName()", message = "successfully changed user display name to $name.")
                result.value = ""
            } else {
                logErrorTask(task, logTag = LOG_TAG, functionName = "updateDisplayName()", message = "error changing user display name to $name.")
                result.value = task.exception.toString()
            }
        }
        return result
    }

    fun updatePassword(newPassword: String): MutableLiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        mAuth?.currentUser?.updatePassword(newPassword)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                logDebug(logTag = LOG_TAG, functionName = "updatePassword()", message = "successfully updated password.")
                result.value = ""
            } else {
                logErrorTask(task, logTag = LOG_TAG, functionName = "updatePassword()", message = "error updating user password")
                result.value = task.exception.toString()
            }
        }
        return result
    }

    fun sendEmailVerification(): LiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        mAuth?.currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                logDebug(logTag = LOG_TAG, functionName = "sendEmailVerification()", message = "successfully sent verification email.")
                result.value = ""
            } else {
                logErrorTask(task, logTag = LOG_TAG, functionName = "sendEmailVerification()", message = "error sending email verification.")
                result.value = task.exception.toString()
            }
        }
        return result
    }

    fun sendPasswordResetEmail(email: String): MutableLiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        mAuth?.sendPasswordResetEmail(email)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                logDebug(logTag = LOG_TAG, functionName = "sendPasswordResetEmail()", message = "successfully sent password reset email to $email.")
                result.value = ""
            } else {
                logErrorTask(task, logTag = LOG_TAG, functionName = "sendPasswordResetEmail()", message = "error sending password reset email to $email.")
                result.value = task.exception.toString()
            }
        }
        return result
    }

    /**
     *  createAccountWithEmailAndPassword() automatically logs user in. While this is not the end functionality we want, we can leverage this to do account setup work.
     */
    private fun setupNewAccount(displayName: String) {
        val newUserMap: MutableMap<String, Any> = HashMap()
        newUserMap["${DC.are_values_set}"] = false
        newUserMap["${DC.bid_house}"] = ""
        newUserMap["${DC.current_round}"] = 0
        overwriteUserInformation(newUserMap)
        updateDisplayName(displayName)
    }

    /**
     * Firestore functions
     */

    fun validateUser(): MutableLiveData<UserState> {
        val result: MutableLiveData<UserState> = MutableLiveData()
        var state: UserState
        if (mAuth?.currentUser == null) {
            state = UserState.LoggedOut
            result.value = state
        } else if (!mAuth?.currentUser?.isEmailVerified!!) {
            state = UserState.EmailNotVerified
            result.value = state
        } else {
            fsDb.collection("${DC.users}").document(mAuth?.currentUser?.uid!!).addSnapshotListener(EventListener<DocumentSnapshot> { snapshot, e ->
                if (e != null || snapshot == null || !snapshot.exists()) {
                    logErrorSnapshot(e, logTag = LOG_TAG, functionName = "validateUser()", message = "error retrieving user document snapshot for user values.")
                    state = UserState.LoggedOut
                    result.value = state
                    return@EventListener
                }
                logDebug(logTag = LOG_TAG, functionName = "validateUser()", message = "successfully retrieved user document snapshot for user validation.")
                result.value = if (snapshot.data?.get("${DC.are_values_set}") as Boolean) UserState.LoggedIn else UserState.ValuesNotSet
            })
        }
        return result
    }

    @Suppress("UNCHECKED_CAST")
    fun getUserValues(): MutableLiveData<ArrayList<String?>> {
        val result: MutableLiveData<ArrayList<String?>> = MutableLiveData()
        fsDb.collection("${DC.users}").document(mAuth?.currentUser?.uid!!).addSnapshotListener(EventListener<DocumentSnapshot> { snapshot, e ->
            if (e != null || snapshot == null || !snapshot.exists()) {
                logErrorSnapshot(e, logTag = LOG_TAG, functionName = "getUserValues()", message = "error retrieving user document snapshot for user values.")
                result.value = arrayListOf()
                return@EventListener
            }
            logDebug(logTag = LOG_TAG, functionName = "getUserValues()", message = "successfully retrieved user document snapshot for user values.")
            result.value = snapshot.data?.get("${DC.values}") as? ArrayList<String?>
        })
        return result
    }

    fun getScheduleMetaData(): MutableLiveData<Triple<Long?, Boolean, String?>> {
        val result: MutableLiveData<Triple<Long?, Boolean, String?>> = MutableLiveData()
        fsDb.collection("${DC.users}").document(mAuth?.currentUser?.uid!!).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                logDebug(logTag = LOG_TAG, functionName = "getScheduleMetaData()", message = "successfully retrieved user document for user schedule.")
                val userData = task.result?.data
                result.value = Triple(userData?.get("${DC.current_round}") as? Long?, userData?.get("${DC.current_schedule}") != null, userData?.get("${DC.bid_house}") as? String?)
            } else {
                logErrorTask(task, logTag = LOG_TAG, functionName = "getScheduleMetaData()", message = "error retrieving user document for user schedule.")
                result.value = Triple(-1, false, "")
            }
        }
        return result
    }

    @Suppress("UNCHECKED_CAST")
    fun getSchedule(): MutableLiveData<HashMap<String, HashMap<String, String>>> {
        val result: MutableLiveData<HashMap<String, HashMap<String, String>>> = MutableLiveData()
        fsDb.collection("${DC.users}").document(mAuth?.currentUser?.uid!!).addSnapshotListener(EventListener<DocumentSnapshot> { snapshot, e ->
            if (e != null || snapshot == null || !snapshot.exists()) {
                logErrorSnapshot(e, logTag = LOG_TAG, functionName = "getSchedule()", message = "error retrieving user document snapshot for schedule.")
                result.value = hashMapOf()
                return@EventListener
            }
            logDebug(logTag = LOG_TAG, functionName = "getSchedule()", message = "successfully retrieved user document snapshot for schedule.")
            result.value = snapshot.data?.get("${DC.current_schedule}") as? HashMap<String, HashMap<String, String>>
                    ?: hashMapOf()
        })
        return result
    }

    @Suppress("UNCHECKED_CAST")
    fun getCurrentRanking(): MutableLiveData<HashMap<String, Int>> {
        val result: MutableLiveData<HashMap<String, Int>> = MutableLiveData()
        fsDb.collection("${DC.users}").document(mAuth?.currentUser?.uid!!).addSnapshotListener(EventListener<DocumentSnapshot> { snapshot, e ->
            if (e != null || snapshot == null || !snapshot.exists()) {
                logErrorSnapshot(e, logTag = LOG_TAG, functionName = "getCurrentRanking()", message = "error retrieving user document snapshot for current ranking.")
                result.value = null
                return@EventListener
            }
            logDebug(logTag = LOG_TAG, functionName = "getCurrentRanking()", message = "successfully retrieved user document for current ranking.")
            result.value = snapshot.data?.get("${DC.current_ranking}") as? HashMap<String, Int>
            return@EventListener
        })
        return result
    }

    @Suppress("UNCHECKED_CAST")
    fun getNote(houseId: String): MutableLiveData<HashMap<String, Any>> {
        val result: MutableLiveData<HashMap<String, Any>> = MutableLiveData()
        fsDb.collection("${DC.users}").document(mAuth?.currentUser?.uid!!).addSnapshotListener(EventListener<DocumentSnapshot> { snapshot, e ->
            if (e != null || snapshot == null || !snapshot.exists()) {
                logErrorSnapshot(e, logTag = LOG_TAG, functionName = "getNote()", message = "error retrieving notes snapshot at $houseId.")
                result.value = null
                return@EventListener
            }
            logDebug(logTag = LOG_TAG, functionName = "getNote()", message = "successfully retrieved notes snapshot at $houseId.")
            result.value = snapshot.data as? HashMap<String, Any>
            return@EventListener
        })
        return result
    }

    /*
        This is intentionally not a snapshot listener in order to reduce the number of server connections
     */
    fun getStaticPanhelValues(): MutableLiveData<ArrayList<*>> {
        val result: MutableLiveData<ArrayList<*>> = MutableLiveData()
        fsDb.collection("${DC.panhel_data}").document("${DC.panhel_values}").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                logDebug(logTag = LOG_TAG, functionName = "getStaticPanhelValues()", message = "successfully retrieved document for panhel values.")
                result.value = task.result?.data?.get("${DC.values}") as? ArrayList<*>
            } else {
                logErrorTask(task, logTag = LOG_TAG, functionName = "getStaticPanhelValues()", message = "error retrieving document for panhel values.")
                result.value = arrayListOf<String>()
            }
        }
        return result
    }

    /*
        This is intentionally not a snapshot listener in order to reduce the number of server connections
     */
    @Suppress("UNCHECKED_CAST")
    fun getStaticHouseData(): MutableLiveData<HashMap<String, HashMap<String, String>>> {
        val result: MutableLiveData<HashMap<String, HashMap<String, String>>> = MutableLiveData()
        fsDb.collection("${DC.panhel_data}").document("${DC.house_information}").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                logDebug(logTag = LOG_TAG, functionName = "getStaticHouseData()", message = "successfully retrieved document for static house data.")
                result.value = task.result?.data as? HashMap<String, HashMap<String, String>>
            } else {
                logErrorTask(task, logTag = LOG_TAG, functionName = "getStaticHouseData()", message = "error retrieving document for static house data.")
                result.value = null
            }
        }
        return result
    }

    fun updateUserInformation(values: MutableMap<String, Any>): MutableLiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        fsDb.collection("${DC.users}").document(mAuth?.currentUser?.uid!!).update(values)
                .addOnSuccessListener {
                    logDebug(logTag = LOG_TAG, functionName = "updateUserInformation()", message = "successfully updated user document with $values.")
                    result.value = ""
                }
                .addOnFailureListener { exception ->
                    logError(logTag = LOG_TAG, functionName = "updateUserInformation()", message = "error updating user document with $values.")
                    result.value = exception.toString()
                }
        return result
    }

    fun updateRanking(updatedRanking: HashMap<String, Int>): MutableLiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        val rankingUpdatesMap = HashMap<String, Any>()
        for ((rankingKey, rankingValue) in updatedRanking) {
            rankingUpdatesMap["${DC.current_ranking}.$rankingKey"] = rankingValue
        }
        fsDb.collection("${DC.users}").document(mAuth?.currentUser?.uid!!).update(rankingUpdatesMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                logDebug(logTag = LOG_TAG, functionName = "updateRanking()", message = "successfully updated user ranking with $updatedRanking.")
                result.value = ""
            } else {
                logErrorTask(task, logTag = LOG_TAG, functionName = "updateRanking()", message = "error updating user ranking with $updatedRanking.")
                result.value = task.exception.toString()
            }
        }
        return result
    }

    fun updateNote(houseId: String, comments: String, valueOne: Boolean, valueTwo: Boolean, valueThree: Boolean) {
        val saveNoteUpdatesMap = HashMap<String, Any>()
        saveNoteUpdatesMap["${DC.notes}.$houseId.${DC.comments}"] = comments
        saveNoteUpdatesMap["${DC.notes}.$houseId.${DC.value1}"] = valueOne
        saveNoteUpdatesMap["${DC.notes}.$houseId.${DC.value2}"] = valueTwo
        saveNoteUpdatesMap["${DC.notes}.$houseId.${DC.value3}"] = valueThree
        val userDoc = fsDb.collection("${DC.users}").document(mAuth?.currentUser?.uid!!)
        userDoc.update(saveNoteUpdatesMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                logDebug(logTag = LOG_TAG, functionName = "updateNote()", message = "successfully updated note for house $houseId in user notes.")
            } else {
                logErrorTask(task, logTag = LOG_TAG, functionName = "submitNote()", message = "error updating note for house $houseId in user notes.")
            }
        }
    }

    fun submitNote(houseIndex: String, houseId: String, comments: String, valueOne: Boolean, valueTwo: Boolean, valueThree: Boolean): MutableLiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        val saveNoteUpdatesMap = HashMap<String, Any>()
        saveNoteUpdatesMap["${DC.notes}.$houseId.${DC.comments}"] = comments
        saveNoteUpdatesMap["${DC.notes}.$houseId.${DC.value1}"] = valueOne
        saveNoteUpdatesMap["${DC.notes}.$houseId.${DC.value2}"] = valueTwo
        saveNoteUpdatesMap["${DC.notes}.$houseId.${DC.value3}"] = valueThree
        val userDoc = fsDb.collection("${DC.users}").document(mAuth?.currentUser?.uid!!)
        userDoc.update(saveNoteUpdatesMap).addOnCompleteListener { task1 ->
            if (task1.isSuccessful) {
                logDebug(logTag = LOG_TAG, functionName = "submitNote()", message = "successfully saved notes for house $houseId to user notes.")
                val rankingUpdatesMap = HashMap<String, Any>()
                rankingUpdatesMap["${DC.current_ranking}.$houseId"] = -1
                userDoc.update(rankingUpdatesMap).addOnCompleteListener { task2 ->
                    if (task2.isSuccessful) {
                        logDebug(logTag = LOG_TAG, functionName = "submitNote()", message = "successfully added house $houseId to user ranking.")
                        val removeIndexUpdatesMap = HashMap<String, Any>()
                        removeIndexUpdatesMap["${DC.current_schedule}.$houseIndex"] = FieldValue.delete()
                        userDoc.update(removeIndexUpdatesMap).addOnCompleteListener { task3 ->
                            if (task3.isSuccessful) {
                                logDebug(logTag = LOG_TAG, functionName = "submitNote()", message = "successfully removed house $houseId from user schedule.")
                                result.value = ""
                            } else {
                                logErrorTask(task3, logTag = LOG_TAG, functionName = "submitNote()", message = "error removing house $houseId from user schedule.")
                                result.value = task3.exception.toString()
                            }
                        }
                    } else {
                        logErrorTask(task2, logTag = LOG_TAG, functionName = "submitNote()", message = "error adding house $houseId to user ranking.")
                        result.value = task2.exception.toString()
                    }
                }
            } else {
                logErrorTask(task1, logTag = LOG_TAG, functionName = "submitNote()", message = "error saving notes for house $houseId to user notes.")
                result.value = task1.exception.toString()
            }
        }
        return result
    }

    private fun overwriteUserInformation(values: MutableMap<String, Any>): LiveData<String> {
        val result: MutableLiveData<String> = MutableLiveData()
        fsDb.collection("${DC.users}").document(mAuth?.currentUser?.uid!!).set(values)
                .addOnSuccessListener {
                    logDebug(logTag = LOG_TAG, functionName = "overwriteUserInformation()", message = "successfully overwrote user document with $values.")
                    result.value = ""
                }
                .addOnFailureListener { exception ->
                    logError(exception = exception, logTag = LOG_TAG, functionName = "overwriteUserInformation()", message = "error overwriting user document with $values.")
                    result.value = exception.toString()
                }
        return result
    }

    /**
     *   Storage functions
     */

    fun getStaticHouseImageReference(fileName: String): StorageReference {
        return storage.reference.child("${DC.house_images}").child("$fileName.jpg")
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