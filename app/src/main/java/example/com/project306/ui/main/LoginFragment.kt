package example.com.project306.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import example.com.project306.R
import example.com.project306.databinding.FragmentLoginBinding
import example.com.project306.util.InjectorUtils
import kotlinx.android.synthetic.main.fragment_login.*

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
        attempt_login_button.setOnClickListener {
            if (validInput(login_enter_email.text.toString(), login_enter_password.text.toString())) {
                toggleLoginProgressBar(true)
                loginFragmentViewModel.attemptLogin(login_enter_email.text.toString(), login_enter_password.text.toString()).observe(this, Observer { authResultError ->
                    run {
                        if (authResultError == "") {
                            Navigation.findNavController(view).navigate(R.id.action_login_to_mainFragment, null)
                        } else {
                            Log.i(LOG_TAG, "Firebase authentication error: $authResultError")
                            toggleLoginProgressBar(false)
                            login_enter_email.error = getString(R.string.login_error_default_message)
                            login_enter_email.requestFocus()
                        }
                    }
                })
            } else {
                login_enter_email.error = getString(R.string.login_error_default_message)
                login_enter_email.requestFocus()
            }
        }
        create_account_button.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_login_to_signUpFragment, null)
        }
    }

    private fun validInput(email: String?, password: String?): Boolean {
        return !email.isNullOrEmpty() && !password.isNullOrEmpty() && email!!.contains('@') && email.contains('.')
    }

    private fun toggleLoginProgressBar(showProgress: Boolean) {
        attempt_login_button.visibility = if (showProgress) View.GONE else View.VISIBLE
        attempt_login_progress_bar.visibility = if (showProgress) View.VISIBLE else View.GONE
    }

    companion object {
        private val LOG_TAG: String = LoginFragment::class.java.name
    }
}
