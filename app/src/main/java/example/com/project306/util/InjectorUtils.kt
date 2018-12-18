package example.com.project306.util

import example.com.project306.data.MainRepository
import example.com.project306.ui.main.*

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

    fun provideLoginStartViewModelFactory(): LoginStartViewModelFactory {
        val repository: MainRepository = getMainRepositorySingleton()
        return LoginStartViewModelFactory(repository)
    }

    fun provideMainActivityViewModelFactory(): MainActivityViewModelFactory {
        val repository: MainRepository = getMainRepositorySingleton()
        return MainActivityViewModelFactory(repository)
    }

    fun provideSettingsViewModelFactory(): SettingsViewModelFactory {
        val repository: MainRepository = getMainRepositorySingleton()
        return SettingsViewModelFactory(repository)
    }

    fun provideChooseValuesViewModelFactory(): ChooseValuesViewModelFactory {
        val repository: MainRepository = getMainRepositorySingleton()
        return ChooseValuesViewModelFactory(repository)
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