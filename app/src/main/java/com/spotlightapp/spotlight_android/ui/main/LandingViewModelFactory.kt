package com.spotlightapp.spotlight_android.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LandingViewModelFactory : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LandingViewModel() as T
    }
}