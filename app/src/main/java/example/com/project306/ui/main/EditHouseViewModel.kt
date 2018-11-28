package example.com.project306.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import example.com.project306.data.MainRepository

class EditHouseViewModel(private val mainRepository: MainRepository) : ViewModel() {
    val title: MutableLiveData<String> = MutableLiveData()
    val greekLetters: MutableLiveData<String> = MutableLiveData()
}