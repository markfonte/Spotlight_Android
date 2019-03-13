package com.spotlightapp.spotlight_android.util

import com.spotlightapp.spotlight_android.data.MainRepository
import com.spotlightapp.spotlight_android.ui.main.*

object InjectorUtils {

    private fun getMainRepositorySingleton(): MainRepository {
        return MainRepository.getInstance()
    }

    fun provideHomeViewModelFactory(): HomeViewModelFactory {
        val repository: MainRepository = getMainRepositorySingleton()
        return HomeViewModelFactory(repository)
    }

    fun provideLoginViewModelFactory(): LoginViewModelFactory {
        val repository: MainRepository = getMainRepositorySingleton()
        return LoginViewModelFactory(repository)
    }

    fun provideSignUpViewModelFactory(): SignUpViewModelFactory {
        val repository: MainRepository = getMainRepositorySingleton()
        return SignUpViewModelFactory(repository)
    }

    fun provideLandingViewModelFactory(): LandingViewModelFactory {
        val repository: MainRepository = getMainRepositorySingleton()
        return LandingViewModelFactory(repository)
    }

    fun provideMainActivityViewModelFactory(): MainActivityViewModelFactory {
        val repository: MainRepository = getMainRepositorySingleton()
        return MainActivityViewModelFactory(repository)
    }

    fun provideSettingsViewModelFactory(): SettingsViewModelFactory {
        val repository: MainRepository = getMainRepositorySingleton()
        return SettingsViewModelFactory(repository)
    }

    fun provideOnboardingViewModelFactory(): OnboardingViewModelFactory {
        val repository: MainRepository = getMainRepositorySingleton()
        return OnboardingViewModelFactory(repository)
    }

    fun provideRankingViewModelFactory(): RankingViewModelFactory {
        val repository: MainRepository = getMainRepositorySingleton()
        return RankingViewModelFactory(repository)
    }

    fun provideScheduleViewModelFactory(): ScheduleViewModelFactory {
        val mainRepository: MainRepository = getMainRepositorySingleton()
        return ScheduleViewModelFactory(mainRepository)
    }

    fun provideNotesViewModelFactory(): NotesViewModelFactory {
        val mainRepository: MainRepository = getMainRepositorySingleton()
        return NotesViewModelFactory(mainRepository)
    }
}