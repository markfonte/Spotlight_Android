package example.com.project306.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import example.com.project306.data.MainRepository

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {
    fun areValuesSet(): LiveData<Boolean> {
        return mainRepository.areValuesSet()
    }
}
