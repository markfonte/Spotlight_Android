package example.com.project306.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import example.com.project306.data.MainRepository

class SororityScheduleViewModel(private val mainRepository: MainRepository) : ViewModel() {
    fun getSchedule(scheduleName: String): LiveData<ArrayList<HashMap<String, String>>> {
        return mainRepository.getSchedule(scheduleName)
    }

    var isDataToDisplay: MutableLiveData<Boolean> = MutableLiveData()
    var isDataLoading: MutableLiveData<Boolean> = MutableLiveData()

    init {
        isDataToDisplay.value = false
        isDataLoading.value = true
    }
}