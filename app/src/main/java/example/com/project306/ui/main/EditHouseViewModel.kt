package example.com.project306.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import example.com.project306.data.MainRepository

class EditHouseViewModel(private val mainRepository: MainRepository) : ViewModel() {
    val displayName: MutableLiveData<String> = MutableLiveData()
    val greekLetters: MutableLiveData<String> = MutableLiveData()
    val streetAddress: MutableLiveData<String> = MutableLiveData()
    val valueOne: MutableLiveData<String> = MutableLiveData()
    val valueTwo: MutableLiveData<String> = MutableLiveData()
    val valueThree: MutableLiveData<String> = MutableLiveData()

    fun getUserValues(): MutableLiveData<ArrayList<String>> {
        return mainRepository.getUserValues()
    }

    fun setBottomNavVisibility(makeVisible: Boolean) {
        mainRepository.isBottomNavVisible.value = makeVisible
    }
}