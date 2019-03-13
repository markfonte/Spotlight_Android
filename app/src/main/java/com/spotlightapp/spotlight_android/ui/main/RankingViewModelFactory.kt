package com.spotlightapp.spotlight_android.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.spotlightapp.spotlight_android.data.MainRepository

class RankingViewModelFactory(private val repository: MainRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RankingViewModel(repository) as T
    }
}