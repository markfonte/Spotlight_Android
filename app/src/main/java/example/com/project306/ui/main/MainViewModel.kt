package example.com.project306.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val mDisplayName: MutableLiveData<String> = MutableLiveData()

    fun getDisplayName() : MutableLiveData<String> {
        return mDisplayName
    }
}
