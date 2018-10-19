package example.com.project306.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import example.com.project306.data.MainRepository

class MainActivityViewModel(mainRepository: MainRepository) : ViewModel() {
    var isBottomNavVisible: MutableLiveData<Boolean> = MutableLiveData()

    init {
        isBottomNavVisible.value = false
    }
}