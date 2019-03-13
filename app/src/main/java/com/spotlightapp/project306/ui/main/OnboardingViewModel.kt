package com.spotlightapp.project306.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.spotlightapp.project306.data.MainRepository

class OnboardingViewModel(private val mainRepository: MainRepository) : ViewModel() {
    var panhelValues: LiveData<ArrayList<*>> = mainRepository.panhelValues
    var currentlyCheckedBoxes: HashMap<Int, String>? = HashMap()
    var submittedCheckboxes: ArrayList<String>? = ArrayList()

    fun submitChosenValues(values: MutableMap<String, Any>): LiveData<String> {
        return mainRepository.updateUserInformation(values)
    }
}