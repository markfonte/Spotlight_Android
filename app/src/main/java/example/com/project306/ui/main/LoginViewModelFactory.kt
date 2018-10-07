package example.com.project306.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import example.com.project306.data.MainRepository

class LoginViewModelFactory(private val repository: MainRepository): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginViewModel(repository) as T
    }
}