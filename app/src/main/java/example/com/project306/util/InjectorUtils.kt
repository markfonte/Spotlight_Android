package example.com.project306.util

import example.com.project306.data.MainRepository
import example.com.project306.ui.main.*

object InjectorUtils {

    private fun getMainRepositorySingleton(): MainRepository {
        return MainRepository.getInstance()
    }

    fun provideMainViewModelFactory(): HomeViewModelFactory {
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

    fun provideEditRankingViewModelFactory(): EditRankingViewModelFactory {
        val repository: MainRepository = getMainRepositorySingleton()
        return EditRankingViewModelFactory(repository)
    }

    fun provideSororityScheduleViewModelFactory(): SororityScheduleViewModelFactory {
        val mainRepository: MainRepository = getMainRepositorySingleton()
        return SororityScheduleViewModelFactory(mainRepository)
    }

    fun provideEditHouseViewModelFactory(): EditHouseViewModelFactory {
        val mainRepository: MainRepository = getMainRepositorySingleton()
        return EditHouseViewModelFactory(mainRepository)
    }
}