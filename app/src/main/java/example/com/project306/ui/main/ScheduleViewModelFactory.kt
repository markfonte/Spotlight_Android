package example.com.project306.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import example.com.project306.data.MainRepository

class ScheduleViewModelFactory(private val mainRepository: MainRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ScheduleViewModel(mainRepository) as T
    }
}