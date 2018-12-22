package example.com.project306.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import example.com.project306.data.MainRepository

class OnboardingViewModel(private val mainRepository: MainRepository) : ViewModel() {
    var panhelValues: LiveData<ArrayList<*>> = mainRepository.getPanhelValues()

    fun submitChosenValues(values: MutableMap<String, Any>): LiveData<String> {
        return mainRepository.overwriteUserInformation(values)
    }
}