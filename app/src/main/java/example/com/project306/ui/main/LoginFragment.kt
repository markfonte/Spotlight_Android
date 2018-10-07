package example.com.project306.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import example.com.project306.R
import example.com.project306.databinding.FragmentLoginBinding
import example.com.project306.util.InjectorUtils

class LoginFragment : Fragment() {

    private lateinit var loginFragmentViewModel: LoginViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val factory: LoginViewModelFactory = InjectorUtils.provideLoginViewModelFactory()
        loginFragmentViewModel = ViewModelProviders.of(this, factory).get(LoginViewModel::class.java)
        val binding = DataBindingUtil.inflate<FragmentLoginBinding>(inflater, R.layout.fragment_login, container, false).apply {
            viewModel = loginFragmentViewModel
            setLifecycleOwner(this@LoginFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
