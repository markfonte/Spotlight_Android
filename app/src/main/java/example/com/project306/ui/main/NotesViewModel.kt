package example.com.project306.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.StorageReference
import example.com.project306.data.MainRepository

class NotesViewModel(private val mainRepository: MainRepository) : ViewModel() {
    val displayName: MutableLiveData<String> = MutableLiveData()
    val greekLetters: MutableLiveData<String> = MutableLiveData()
    val streetAddress: MutableLiveData<String> = MutableLiveData()
    val houseId: MutableLiveData<String> = MutableLiveData()
    val valueOne: MutableLiveData<String> = MutableLiveData()
    val valueTwo: MutableLiveData<String> = MutableLiveData()
    val valueThree: MutableLiveData<String> = MutableLiveData()

    fun getUserValues(): MutableLiveData<ArrayList<String?>> {
        return mainRepository.getUserValues()
    }

    fun setBottomNavVisibility(makeVisible: Boolean) {
        mainRepository.isBottomNavVisible.value = makeVisible
    }

    fun getStaticHouseImageReference(fileName: String): StorageReference {
        return mainRepository.getStaticHouseImageReference(fileName)
    }
}