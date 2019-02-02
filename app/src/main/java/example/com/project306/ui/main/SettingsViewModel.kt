package example.com.project306.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import example.com.project306.data.MainRepository

class SettingsViewModel(private val mainRepository: MainRepository) : ViewModel() {
    val valueOne: MutableLiveData<String> = MutableLiveData()
    val valueTwo: MutableLiveData<String> = MutableLiveData()
    val valueThree: MutableLiveData<String> = MutableLiveData()
    val displayName: MutableLiveData<String> = mainRepository.getDisplayName()
    val email: MutableLiveData<String> = mainRepository.getEmail()
    var tempDisplayName: String = ""

    fun logout(): MutableLiveData<String> {
        return mainRepository.accountsLogout()
    }

    fun getUserValues(): MutableLiveData<ArrayList<String?>> {
        return mainRepository.getUserValues()
    }

    fun changeDisplayName(name: String): MutableLiveData<String> {
        return mainRepository.changeDisplayName(name)
    }

    fun reauthenticateUser(password: String): MutableLiveData<String> {
        return mainRepository.reauthenticateUser(password)
    }

    fun updatePassword(newPassword: String): MutableLiveData<String> {
        return mainRepository.updatePassword(newPassword)
    }

    fun areValuesSet(): LiveData<Boolean> {
        return mainRepository.areValuesSet()
    }

    fun setAppBarVisibility(makeVisible: Boolean) {
        mainRepository.isAppBarVisible.value = makeVisible
    }

    fun setBottomNavVisibility(makeVisible: Boolean) {
        mainRepository.isBottomNavVisible.value = makeVisible
    }
}