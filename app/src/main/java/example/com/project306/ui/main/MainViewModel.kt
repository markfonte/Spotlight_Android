package example.com.project306.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import example.com.project306.data.MainRepository
import example.com.project306.util.SororityTimeSlot

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {
    var mDisplayName: MutableLiveData<String> = MutableLiveData()
    private lateinit var sororityTimeSlots: MutableLiveData<List<SororityTimeSlot>>
    private var panhelValues: LiveData<ArrayList<*>> = mainRepository.getPanhelValues()
}
