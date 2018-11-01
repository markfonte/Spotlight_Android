package example.com.project306.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import example.com.project306.data.MainRepository
import example.com.project306.data.SororityScheduleRepository

class SororityScheduleViewModelFactory(private val mainRepository: MainRepository, private val sororityScheduleRepository: SororityScheduleRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SororityScheduleViewModel(mainRepository, sororityScheduleRepository) as T
    }
}