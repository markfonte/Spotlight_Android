package example.com.project306.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import example.com.project306.data.MainRepository

class ScheduleViewModel(private val mainRepository: MainRepository) : ViewModel() {
    fun getSchedule(scheduleName: String): LiveData<ArrayList<HashMap<String, String>>> {
        return mainRepository.getSchedule(scheduleName)
    }

    var isScheduleToDisplay: MutableLiveData<Boolean> = MutableLiveData()
    var isDataLoading: MutableLiveData<Boolean> = MutableLiveData()
    var staticHouseData: MutableLiveData<HashMap<String, HashMap<String, String>>> = mainRepository.staticHouseData
    var noScheduleMessage: MutableLiveData<String> = MutableLiveData()

    init {
        isScheduleToDisplay.value = false
        isDataLoading.value = true
        noScheduleMessage.value = "No schedule to display right now. Please check back later!"
    }
}