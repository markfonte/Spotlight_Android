package com.spotlightapp.spotlight_android.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.spotlightapp.spotlight_android.data.MainRepository

class ScheduleViewModel(private val mainRepository: MainRepository) : ViewModel() {
    fun getCurrentSchedule(): MutableLiveData<HashMap<String, HashMap<String, String>>> {
        return mainRepository.getSchedule()
    }

    var isScheduleToDisplay: MutableLiveData<Boolean> = MutableLiveData()
    var isDataLoading: MutableLiveData<Boolean> = MutableLiveData()
    var staticHouseData: MutableLiveData<HashMap<String, HashMap<String, String>>> = mainRepository.staticHouseData
    var noScheduleMessage: MutableLiveData<String> = MutableLiveData()
    var position: Int = -1
    var displayMode: Int = -1
    var bidHouse: String? = ""
    var currentRoundName: String? = ""

    init {
        isScheduleToDisplay.value = false
        isDataLoading.value = true
        noScheduleMessage.value = "No schedule to display right now. Please check back later!"
    }
}