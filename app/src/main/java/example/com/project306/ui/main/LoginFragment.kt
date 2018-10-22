package example.com.project306.ui.main

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
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
        hideKeyboard(view)
        if (isValidInput(login_enter_email.text.toString(), login_enter_password.text.toString())) {
            toggleLoginProgressBar(true)
            loginFragmentViewModel.attemptLogin(login_enter_email.text.toString(), login_enter_password.text.toString()).observe(this, Observer { authResultError ->
                run {
                    if (authResultError == "") {
                        val navOptions = NavOptions.Builder().setPopUpTo(R.id.mainFragment, true).build()
                        Navigation.findNavController(view).navigate(R.id.action_login_to_mainFragment, null, navOptions)
                    } else {
                        Log.i(LOG_TAG, "Firebase authentication error: $authResultError")
                        toggleLoginProgressBar(false)
                        login_enter_email.error = getString(R.string.login_error_default_message)
                        login_enter_email.requestFocus()
                        showKeyboard()
                    }
                }
            })
        } else {
            login_enter_email.error = getString(R.string.login_error_default_message)
            login_enter_email.requestFocus()
        }
    }

    private fun showKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    private fun hideKeyboard(view: View) {
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
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
