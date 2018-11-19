package example.com.project306.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import example.com.project306.data.MainRepository
import example.com.project306.data.SororityScheduleRepository

class SororityScheduleViewModel(private val mainRepository: MainRepository, private val sororityScheduleRepository: SororityScheduleRepository) : ViewModel() {
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