package example.com.project306.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import example.com.project306.R
import example.com.project306.databinding.FragmentLoginStartBinding
import example.com.project306.util.InjectorUtils
import kotlinx.android.synthetic.main.fragment_login_start.*

class LoginStartFragment : Fragment() {

    private lateinit var loginStartFragmentViewModel: LoginStartViewModel
    private var testBool: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val factory: LoginStartViewModelFactory = InjectorUtils.provideLoginStartViewModelFactory()
        loginStartFragmentViewModel = ViewModelProviders.of(this, factory).get(LoginStartViewModel::class.java)
        val binding = DataBindingUtil.inflate<FragmentLoginStartBinding>(inflater, R.layout.fragment_login_start, container, false).apply {
            viewModel = loginStartFragmentViewModel
            setLifecycleOwner(this@LoginStartFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        login_start_login_button.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_loginStartFragment_to_loginFragment, null)
        }
        login_start_sign_up_button.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_loginStartFragment_to_signUpFragment, null)
        }
    }

    companion object {
        private val LOG_TAG: String = LoginStartFragment::class.java.name
    }
}