package example.com.project306.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import example.com.project306.data.MainRepository

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {
    var staticHouseData: MutableLiveData<HashMap<String, HashMap<String, String>>> = mainRepository.staticHouseData

    fun areValuesSet(): LiveData<Boolean> {
        return mainRepository.areValuesSet()
    }

    fun setAppBarVisibility(makeVisible: Boolean) {
        mainRepository.isAppBarVisible.value = makeVisible
    }

    fun setBottomNavVisibility(makeVisible: Boolean) {
        mainRepository.isBottomNavVisible.value = makeVisible
    }
}
