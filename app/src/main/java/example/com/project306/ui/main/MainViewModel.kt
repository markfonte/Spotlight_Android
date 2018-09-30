package example.com.project306.ui.main

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val mDisplayName: MutableLiveData<String> = MutableLiveData()

    fun getDisplayName() : MutableLiveData<String> {
        return mDisplayName
    }
}
