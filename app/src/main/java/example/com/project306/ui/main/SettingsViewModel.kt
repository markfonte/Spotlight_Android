package example.com.project306.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import example.com.project306.data.MainRepository

class SettingsViewModel(private val mainRepository: MainRepository) : ViewModel() {
    fun logout() : MutableLiveData<String> {
        return mainRepository.accountsLogout()
    }
}