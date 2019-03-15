package com.spotlightapp.spotlight_android.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.spotlightapp.spotlight_android.adapter.RankingRecyclerAdapter
import com.spotlightapp.spotlight_android.data.MainRepository

class RankingViewModel(private val mainRepository: MainRepository) : ViewModel() {
    var isDataLoading: MutableLiveData<Boolean> = MutableLiveData()
    var isRankingToDisplay: MutableLiveData<Boolean> = MutableLiveData()
    var staticHouseData: MutableLiveData<HashMap<String, HashMap<String, String>>> = mainRepository.staticHouseData
    var rankingAdapter: RankingRecyclerAdapter? = null

    init {
        isDataLoading.value = true
        isRankingToDisplay.value = false
    }
    fun setBottomNavVisibility(makeVisible: Boolean) {
        mainRepository.isBottomNavVisible.value = makeVisible
    }

    fun setAppBarVisibility(makeVisible: Boolean) {
        mainRepository.isAppBarVisible.value = makeVisible
    }

    fun getCurrentRanking(): MutableLiveData<HashMap<String, Int>> {
        return mainRepository.getCurrentRanking()
    }

    fun updateRanking(updatedRanking: HashMap<String, Int>): MutableLiveData<String> {
        return mainRepository.updateRanking(updatedRanking)
    }
}