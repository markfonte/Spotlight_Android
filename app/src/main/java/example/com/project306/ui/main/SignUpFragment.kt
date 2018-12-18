package example.com.project306.ui.main

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
            attemptSignUp(view)
        }
        sign_up_enter_confirm_password.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                attemptSignUp(view)
                return@OnEditorActionListener true
            }
            false
        })
        sign_up_enter_display_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (signUpFragmentViewModel.showConfirmPassword.value == false) {
                    signUpFragmentViewModel.showConfirmPassword.value = true
                }
            }
        })
        sign_up_enter_confirm_password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (signUpFragmentViewModel.showCreateAccountButton.value == false) {
                    signUpFragmentViewModel.showCreateAccountButton.value = true
                }
            }
        })
        sign_up_main_prompt.setOnClickListener {
            buildInfoDialog()
        }
    }

    private fun buildInfoDialog() {
        val alertDialog: AlertDialog = AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert)
                .setTitle("Help")
                .setMessage(getString(R.string.sign_up_info_message))
                .setPositiveButton("OK", null)
                .create()
        alertDialog.show()
    }

    private fun attemptSignUp(view: View) {
        SystemUtils.hideKeyboard(context, view)
        toggleCreateAccountProgressBar(true)
        val currentDisplayName: String? = sign_up_enter_display_name.text.toString().trim()
        val currentEmail: String? = sign_up_enter_email.text.toString().trim()
        val currentPassword: String? = sign_up_enter_password.text.toString()
        val currentConfirmPassword: String? = sign_up_enter_confirm_password.text.toString()
        if (isValidInput(currentDisplayName, currentEmail, currentPassword, currentConfirmPassword)) {
            signUpFragmentViewModel.attemptCreateAccount(currentEmail!!, currentPassword!!, currentDisplayName!!).observe(this, Observer { error ->
                run {
                    if (error == "") {
                        sendEmailVerification(currentEmail, view)
                    } else {
                        toggleCreateAccountProgressBar(false)
                        Log.i(LOG_TAG, "Error creating account: $error")
                        sign_up_enter_display_name.error = getString(R.string.sign_up_error_default_message)
                        sign_up_enter_display_name.requestFocus()
                        SystemUtils.showKeyboard(activity)
                    }
                }
            })
        } else {
            toggleCreateAccountProgressBar(false)
            Log.i(LOG_TAG, "Poorly formed input")
            sign_up_enter_display_name.error = getString(R.string.sign_up_error_poorly_formed_input)
            sign_up_enter_display_name.requestFocus()
            SystemUtils.showKeyboard(activity)
        }
    }

    private fun sendEmailVerification(email: String, view: View) {
        signUpFragmentViewModel.attemptEmailVerification().observe(this, Observer { error ->
            run {
                toggleCreateAccountProgressBar(false)
                if (error == "") {
                    val snackbar: Snackbar? = Snackbar.make(activity?.findViewById(R.id.sign_up_fragment_container)!!, getString(R.string.sign_up_and_email_verification_success_confirmation), Snackbar.LENGTH_INDEFINITE)
                    SystemUtils.setSnackbarDefaultOptions(snackbar)
                    snackbar?.setAction("OK") {
                        snackbar.dismiss()
                        Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_loginFragment, null)
                    }
                    snackbar?.show()
                } else {
                    Log.e(LOG_TAG, "Verification email was not sent.")
                    val snackbar: Snackbar? = Snackbar.make(activity?.findViewById(R.id.sign_up_fragment_container)!!, getString(R.string.sign_up_email_verification_failed), Snackbar.LENGTH_LONG).setDuration(resources.getInteger(R.integer.CUSTOM_SNACKBAR_LENGTH_LONG))
                    SystemUtils.setSnackbarDefaultOptions(snackbar)
                    snackbar?.setAction("YES") {
                        snackbar.dismiss()
                        sendEmailVerification(email, view)
                    }
                    snackbar?.show()
                }
            }
        })
    }

    private fun isValidInput(displayName: String?, email: String?, password: String?, confirmPassword: String?): Boolean {
        return !displayName.isNullOrEmpty() && !email.isNullOrEmpty() && !password.isNullOrEmpty() && !confirmPassword.isNullOrEmpty() && displayName.length < 30 && email.length < 30 && password.length < 30 && confirmPassword.length < 30 && email.contains('@') && email.contains(".") && password == confirmPassword
    }

    private fun toggleCreateAccountProgressBar(showProgress: Boolean) {
        signUpFragmentViewModel.isCreatingAccount.value = showProgress
        signUpFragmentViewModel.showCreateAccountButton.value = !showProgress
    }

    companion object {
        private val LOG_TAG: String = SignUpFragment::class.java.name
    }
}
