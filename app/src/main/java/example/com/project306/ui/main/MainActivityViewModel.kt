package example.com.project306.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import example.com.project306.data.MainRepository

class MainActivityViewModel( val mainRepository: MainRepository) : ViewModel() {
    var isBottomNavVisible: MutableLiveData<Boolean> = mainRepository.isBottomNavVisible
    var isAppBarVisible: MutableLiveData<Boolean> = mainRepository.isAppBarVisible
    var currentUser: MutableLiveData<FirebaseUser> = mainRepository.getCurrentUser()

    fun setAppBarVisibility(makeVisible: Boolean) {
        mainRepository.isAppBarVisible.value = makeVisible
    }

    fun setBottomNavVisibility(makeVisible: Boolean) {
        mainRepository.isBottomNavVisible.value = makeVisible
    }
}