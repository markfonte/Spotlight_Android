package example.com.project306.ui.main

import android.graphics.Color
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
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import example.com.project306.R
import example.com.project306.databinding.FragmentSignUpBinding
import example.com.project306.util.InjectorUtils
import example.com.project306.util.SystemUtils
import kotlinx.android.synthetic.main.fragment_sign_up.*


class SignUpFragment : Fragment() {

    private lateinit var signUpFragmentViewModel: SignUpViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val factory: SignUpViewModelFactory = InjectorUtils.provideSignUpViewModelFactory()
        signUpFragmentViewModel = ViewModelProviders.of(this, factory).get(SignUpViewModel::class.java)
        val binding = DataBindingUtil.inflate<FragmentSignUpBinding>(inflater, R.layout.fragment_sign_up, container, false).apply {
            viewModel = signUpFragmentViewModel
            setLifecycleOwner(this@SignUpFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sign_up_to_login_button.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_loginFragment, null)
        }
        attempt_create_account_button.setOnClickListener {
            attemptLogin(view)
        }
        sign_up_enter_confirm_password.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                attemptLogin(view)
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun attemptLogin(view: View) {
        SystemUtils.hideKeyboard(context, view)
        val currentDisplayName: String? = sign_up_enter_display_name.text.toString()
        val currentEmail: String? = sign_up_enter_email.text.toString()
        val currentPassword: String? = sign_up_enter_password.text.toString()
        val currentConfirmPassword: String? = sign_up_enter_confirm_password.text.toString()
        if (isValidInput(currentDisplayName, currentEmail, currentPassword, currentConfirmPassword)) {
            signUpFragmentViewModel.attemptCreateAccount(currentEmail!!, currentPassword!!).observe(this, Observer { error ->
                run {
                    if (error == "") {
                        sendEmailVerification(currentEmail, view)
                    } else {
                        Log.i(LOG_TAG, "Error creating account: $error")
                        sign_up_enter_display_name.error = getString(R.string.sign_up_error_default_message)
                        sign_up_enter_display_name.requestFocus()
                        SystemUtils.showKeyboard(activity)
                    }
                }
            })
        } else {
            Log.i(LOG_TAG, "Poorly formed input")
            sign_up_enter_display_name.error = getString(R.string.sign_up_error_poorly_formed_input)
            sign_up_enter_display_name.requestFocus()
            SystemUtils.showKeyboard(activity)
        }
    }

    private fun sendEmailVerification(email: String, view: View) {
        signUpFragmentViewModel.attemptEmailVerification(email).observe(this, Observer { error ->
            run {
                if (error == "") {
                    val snackbar: Snackbar? = Snackbar.make(activity?.findViewById(R.id.sign_up_fragment_container)!!, getString(R.string.sign_up_and_email_verification_success_confirmation), Snackbar.LENGTH_INDEFINITE)
                    val snackbarView: View? = snackbar?.view
                    val snackbarMessage: TextView? = snackbarView?.findViewById(R.id.snackbar_text)
                    snackbarMessage?.setTextColor(Color.WHITE)
                    snackbar?.setAction("OK") {
                        snackbar.dismiss()
                        Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_loginFragment, null)
                    }
                    snackbar?.show()
                } else {
                    Log.e(LOG_TAG, "Verification email was not sent.")
                    val snackbar: Snackbar? = Snackbar.make(activity?.findViewById(R.id.sign_up_fragment_container)!!, getString(R.string.sign_up_email_verification_failed), Snackbar.LENGTH_INDEFINITE)
                    val snackbarView: View? = snackbar?.view
                    val snackbarMessage: TextView? = snackbarView?.findViewById(R.id.snackbar_text)
                    snackbarMessage?.setTextColor(Color.WHITE)
                    snackbar?.setAction("YES") {
                        snackbar.dismiss()
                        sendEmailVerification(email, view)
                    }
                    snackbar?.setAction("NO") { //automatically logs them in
                        snackbar.dismiss()
                        Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_loginFragment, null)
                    }
                    snackbar?.show()
                }
            }
        })
    }

    private fun isValidInput(displayName: String?, email: String?, password: String?, confirmPassword: String?): Boolean {
        return !displayName.isNullOrEmpty() && !email.isNullOrEmpty() && !password.isNullOrEmpty() && !confirmPassword.isNullOrEmpty() && email!!.contains('@') && email.contains(".") && password == confirmPassword
    }

    companion object {
        private val LOG_TAG: String = SignUpFragment::class.java.name
    }
}
