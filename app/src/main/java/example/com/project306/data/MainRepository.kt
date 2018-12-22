package example.com.project306.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.StorageReference

class MainRepository {
    private var firebaseService: FirebaseService = FirebaseService.getInstance()
    var isBottomNavVisible: MutableLiveData<Boolean> = MutableLiveData()
    var isAppBarVisible: MutableLiveData<Boolean> = MutableLiveData()
    var staticHouseData: MutableLiveData<HashMap<String, HashMap<String, String>>> = firebaseService.getStaticHouseData()

    fun refreshStaticHouseData() {
        staticHouseData.value = firebaseService.getStaticHouseData().value
    }

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

    fun attemptCreateAccount(email: String, password: String, displayName: String): LiveData<String> {
        return firebaseService.attemptCreateAccount(email, password, displayName)
    }

    fun attemptEmailVerification(): LiveData<String> {
        return firebaseService.sendEmailVerification()
    }

    fun getPanhelValues(): LiveData<ArrayList<*>> {
        return firebaseService.getPanhelValues()
    }

    fun areValuesSet(): LiveData<Boolean> {
        return firebaseService.areValuesSet()
    }

    fun overwriteUserInformation(values: MutableMap<String, Any>): LiveData<String> {
        return firebaseService.overwriteUserInformation(values)
    }

    fun getSchedule(scheduleName: String): LiveData<ArrayList<HashMap<String, String>>> {
        return firebaseService.getSchedule(scheduleName)
    }

    fun getDisplayName(): MutableLiveData<String> {
        return firebaseService.getDisplayName()
    }

    fun getEmail(): MutableLiveData<String> {
        return firebaseService.getEmail()
    }

    fun getUserValues(): MutableLiveData<ArrayList<String?>> {
        return firebaseService.getUserValues()
    }

    fun sendForgotPasswordEmail(email: String): MutableLiveData<String> {
        return firebaseService.sendForgotPasswordEmail(email)
    }

    fun changeDisplayName(name: String): MutableLiveData<String> {
        return firebaseService.changeDisplayName(name)
    }

    fun reauthenticateUser(password: String): MutableLiveData<String> {
        return firebaseService.reauthenticateUser(password)
    }

    fun updatePassword(newPassword: String): MutableLiveData<String> {
        return firebaseService.updatePassword(newPassword)
    }

    fun getStaticHouseImageReference(fileName: String): StorageReference {
        return firebaseService.getStaticHouseImageReference(fileName)
    }

    fun getCurrentRound(): MutableLiveData<Triple<Long?, Boolean, String?>> {
        return firebaseService.getCurrentRound()
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