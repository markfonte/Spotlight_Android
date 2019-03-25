package com.spotlightapp.spotlight_android.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.viewpager.widget.ViewPager
import com.google.firebase.storage.StorageReference
import com.spotlightapp.spotlight_android.data.MainRepository
import java.util.*

class HomeViewModel(private val mainRepository: MainRepository) : ViewModel() {
    var staticHouseData: MutableLiveData<HashMap<String, HashMap<String, String>>> = mainRepository.staticHouseData
    var scheduleViewPager: ViewPager? = null
    var bidHouse: MutableLiveData<String> = MutableLiveData()
    var houseDisplayName: MutableLiveData<String> = MutableLiveData()
    var houseGreekLetters: MutableLiveData<String> = MutableLiveData()

    fun getStaticHouseImageReference(fileName: String): StorageReference {
        return mainRepository.getStaticHouseImageReference(fileName)
    }

    fun setAppBarVisibility(makeVisible: Boolean) {
        mainRepository.isAppBarVisible.value = makeVisible
    }

    fun setBottomNavVisibility(makeVisible: Boolean) {
        mainRepository.isBottomNavVisible.value = makeVisible
    }

    fun getScheduleMetaData(): MutableLiveData<Triple<Long?, Boolean, String?>> {
        return mainRepository.getScheduleMetaData()
    }

    fun logout(): MutableLiveData<String> {
        return mainRepository.accountsLogout()
    }
}
