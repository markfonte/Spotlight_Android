package example.com.project306.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import example.com.project306.data.MainRepository

class ChooseValuesViewModel(private val mainRepository: MainRepository) : ViewModel() {
    var panhelValues: LiveData<ArrayList<*>> = mainRepository.getPanhelValues()
}