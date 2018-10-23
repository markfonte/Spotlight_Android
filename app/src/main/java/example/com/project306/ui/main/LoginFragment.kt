package example.com.project306.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import example.com.project306.R
import example.com.project306.databinding.FragmentLoginBinding
import example.com.project306.util.InjectorUtils
import example.com.project306.util.SystemUtils
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
            attemptLogin(view)
        }
        create_account_button.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_login_to_signUpFragment, null)
        }
        login_enter_password.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                attemptLogin(view)
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun attemptLogin(view: View) {
        SystemUtils.hideKeyboard(context, view)
        val currentEmail: String? = login_enter_email.text.toString()
        val currentPassword: String? = login_enter_password.text.toString()
        if (isValidInput(currentEmail, currentPassword)) {
            toggleLoginProgressBar(true)
            loginFragmentViewModel.attemptLogin(currentEmail!!, currentPassword!!).observe(this, Observer { authResultError ->
                run {
                    when (authResultError) {
                        "" -> {
                            val navOptions = NavOptions.Builder().setPopUpTo(R.id.mainFragment, true).build()
                            Navigation.findNavController(view).navigate(R.id.action_login_to_mainFragment, null, navOptions)
                        }
                        "email not verified" -> {
                            Log.i(LOG_TAG, "Email not verified")
                            toggleLoginProgressBar(false)
                            login_enter_email.error = "Please verify your email address and log in again."
                            login_enter_email.requestFocus()
                            SystemUtils.showKeyboard(activity)
                        }
                        else -> {
                            Log.i(LOG_TAG, "Firebase authentication error: $authResultError")
                            toggleLoginProgressBar(false)
                            login_enter_email.error = getString(R.string.login_error_default_message)
                            login_enter_email.requestFocus()
                            SystemUtils.showKeyboard(activity)
                        }
                    }
                }
            })
        } else {
            Log.i(LOG_TAG, "Poorly formed login input")
            login_enter_email.error = getString(R.string.login_error_poorly_formed_input)
            login_enter_email.requestFocus()
            SystemUtils.showKeyboard(activity)
        }
    }

    private fun isValidInput(email: String?, password: String?): Boolean {
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
