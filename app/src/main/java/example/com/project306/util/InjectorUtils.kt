package example.com.project306.util

import android.content.Context
import example.com.project306.data.MainRepository
import example.com.project306.ui.main.LoginViewModelFactory
import example.com.project306.ui.main.MainViewModelFactory
import example.com.project306.ui.main.SignUpViewModelFactory

object InjectorUtils {

    private fun getMainRepository() : MainRepository {
        return MainRepository.getInstance()
    }

    fun provideMainViewModelFactory() : MainViewModelFactory {
        val repository : MainRepository = getMainRepository()
        return MainViewModelFactory(repository)
    }

    fun provideLoginViewModelFactory() : LoginViewModelFactory {
        val repository : MainRepository = getMainRepository()
        return LoginViewModelFactory(repository)
    }

    fun provideSignUpViewModelFactory() : SignUpViewModelFactory {
        val repository: MainRepository = getMainRepository()
        return SignUpViewModelFactory(repository)
    }
}