package example.com.project306.util

import android.content.Context
import example.com.project306.data.MainRepository
import example.com.project306.ui.main.MainViewModelFactory

object InjectorUtils {

    private fun getMainRepository() : MainRepository {
        return MainRepository.getInstance()
    }

    fun provideMainViewModelFactory() : MainViewModelFactory {
        val repository : MainRepository = getMainRepository()
        return MainViewModelFactory(repository)
    }


}