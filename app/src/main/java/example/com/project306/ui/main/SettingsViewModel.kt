package example.com.project306.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import example.com.project306.data.MainRepository

class SettingsViewModel(private val mainRepository: MainRepository) : ViewModel() {
    fun logout(): MutableLiveData<String> {
        return mainRepository.accountsLogout()
    }

    fun getUserValues() : MutableLiveData<ArrayList<String>> {
        return mainRepository.getUserValues()
    }

    val valueOne: MutableLiveData<String> = MutableLiveData()
    val valueTwo: MutableLiveData<String> = MutableLiveData()
    val valueThree: MutableLiveData<String> = MutableLiveData()
    val displayName: MutableLiveData<String> = mainRepository.getDisplayName()
    val email: MutableLiveData<String> = mainRepository.getEmail()
}