package example.com.project306.util

import example.com.project306.data.MainRepository
import example.com.project306.ui.main.*

object InjectorUtils {

    private fun getMainRepository(): MainRepository {
        return MainRepository.getInstance()
    }

    fun provideMainViewModelFactory(): MainViewModelFactory {
        val repository: MainRepository = getMainRepository()
        return MainViewModelFactory(repository)
    }

    fun provideLoginViewModelFactory(): LoginViewModelFactory {
        val repository: MainRepository = getMainRepository()
        return LoginViewModelFactory(repository)
    }

    fun provideSignUpViewModelFactory(): SignUpViewModelFactory {
        val repository: MainRepository = getMainRepository()
        return SignUpViewModelFactory(repository)
    }

    fun provideLoginStartViewModelFactory(): LoginStartViewModelFactory {
        val repository: MainRepository = getMainRepository()
        return LoginStartViewModelFactory(repository)
    }

    fun provideMainActivityViewModelFactory(): MainActivityViewModelFactory {
        val repository: MainRepository = getMainRepository()
        return MainActivityViewModelFactory(repository)
    }

    fun provideSettingsViewModelFactory(): SettingsViewModelFactory {
        val repository: MainRepository = getMainRepository()
        return SettingsViewModelFactory(repository)
    }

    fun provideChooseValuesViewModelFactory(): ChooseValuesViewModelFactory {
        val repository: MainRepository = getMainRepository()
        return ChooseValuesViewModelFactory(repository)
    }

    fun provideEditRankingViewModelFactory(): EditRankingViewModelFactory {
        val repository: MainRepository = getMainRepository()
        return EditRankingViewModelFactory(repository)
    }
}