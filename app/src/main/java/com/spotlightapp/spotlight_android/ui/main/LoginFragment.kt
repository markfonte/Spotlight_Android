package com.spotlightapp.spotlight_android.ui.main

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.spotlightapp.spotlight_android.R
import com.spotlightapp.spotlight_android.databinding.FragmentLoginBinding
import com.spotlightapp.spotlight_android.util.InjectorUtils
import com.spotlightapp.spotlight_android.util.SystemUtils
import kotlinx.android.synthetic.main.dialog_enter_email.*
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : Fragment() {

    private lateinit var vm: LoginViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val factory: LoginViewModelFactory = InjectorUtils.provideLoginViewModelFactory()
        vm = ViewModelProviders.of(this, factory).get(LoginViewModel::class.java)
        val binding = DataBindingUtil.inflate<FragmentLoginBinding>(inflater, R.layout.fragment_login, container, false).apply {
            viewModel = vm
            lifecycleOwner = this@LoginFragment
        }
        return binding.root
    }

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attempt_login_button.setOnClickListener {
            attemptLogin(view)
        }
        create_account_button.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_login_to_signUpFragment, null)
        }
        login_enter_password.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                attemptLogin(view)
                return@OnKeyListener true
            }
            false
        })
        login_enter_password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (vm.showLoginButton.value == false) {
                    vm.showLoginButton.value = true
                }
            }
        })
        forgot_password_button.setOnClickListener {
            showForgotPasswordDialog()
        }
    }

    @SuppressLint("InflateParams")
    private fun showForgotPasswordDialog() {
        val alertDialog: AlertDialog = AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert)
                .setTitle("Send Password Reset Email")
                .setView(activity?.layoutInflater?.inflate(R.layout.dialog_enter_email, null))
                .setPositiveButton("Send", null)
                .setNegativeButton("Cancel", null)
                .create()
        alertDialog.setOnDismissListener {
            view?.let { currentView -> SystemUtils.hideKeyboard(context, currentView) }
            activity?.currentFocus?.let { currentFocus -> SystemUtils.hideKeyboard(context, currentFocus) }
            activity?.window?.currentFocus?.let { currentWindowFocus -> SystemUtils.hideKeyboard(context, currentWindowFocus) }
        }
        alertDialog.setOnShowListener {
            SystemUtils.showKeyboard(activity)
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                vm.sendPasswordResetEmail(alertDialog.enter_email_dialog_enter_email.text.toString().trim())
                val s: Snackbar? = Snackbar.make(activity?.findViewById(R.id.login_fragment_container)!!, "Check your email for password reset information!", Snackbar.LENGTH_INDEFINITE)
                SystemUtils.setSnackbarDefaultOptions(s)
                s?.setAction("OK") {
                    s.dismiss()
                }
                s?.show()
                login_enter_password.text?.clear()
                alertDialog.dismiss()
            }
            alertDialog.enter_email_dialog_enter_email.requestFocus()
            alertDialog.enter_email_dialog_enter_email.text = login_enter_email.text
            login_enter_email.text?.length?.let { length -> alertDialog.enter_email_dialog_enter_email.setSelection(length) }
        }
        alertDialog.show()
    }

    private fun attemptLogin(view: View) {
        SystemUtils.hideKeyboardForced(context, view)
        val currentEmail: String? = login_enter_email.text.toString().trim()
        val currentPassword: String? = login_enter_password.text.toString()
        if (isValidInput(currentEmail, currentPassword)) {
            toggleLoginProgressBar(true)
            vm.attemptLogin(currentEmail!!, currentPassword!!).observe(this, Observer { authResultError ->
                run {
                    SystemUtils.hideKeyboardForced(context, view)
                    when (authResultError) {
                        "" -> {
                            val navOptions = NavOptions.Builder().setPopUpTo(R.id.homeFragment, true).build()
                            Navigation.findNavController(view).navigate(R.id.action_login_to_homeFragment, null, navOptions)
                        }
                        "email not verified" -> {
                            Log.i(LOG_TAG, "Email not verified")
                            toggleLoginProgressBar(false)
                            login_enter_email.error = getString(R.string.login_error_email_not_verified)
                            showResendEmailVerificationPopUp(currentEmail, view)
                        }
                        else -> {
                            Log.i(LOG_TAG, "Firebase authentication error: $authResultError")
                            toggleLoginProgressBar(false)
                            login_enter_email.error = getString(R.string.login_error_default_message)
                            login_enter_password.requestFocus()
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

    private fun showResendEmailVerificationPopUp(email: String, view: View) {
        val snackbar: Snackbar? = Snackbar.make(activity?.findViewById(R.id.login_fragment_container)!!, getString(R.string.login_prompt_resend_email_verification), Snackbar.LENGTH_LONG).setDuration(resources.getInteger(R.integer.CUSTOM_SNACKBAR_LENGTH_LONG))
        SystemUtils.setSnackbarDefaultOptions(snackbar)
        snackbar?.setAction("YES") {
            snackbar.dismiss()
            attemptEmailVerification(email, view)
        }
        snackbar?.show()
    }

    private fun attemptEmailVerification(email: String, view: View) {
        vm.attemptEmailVerification().observe(this, Observer { error ->
            run {
                if (error == "") {
                    val s: Snackbar? = Snackbar.make(activity?.findViewById(R.id.login_fragment_container)!!, getString(R.string.login_email_verification_success_confirmation), Snackbar.LENGTH_INDEFINITE)
                    SystemUtils.setSnackbarDefaultOptions(s)
                    s?.setAction("OK") {
                        s.dismiss()
                    }
                    s?.show()
                } else {
                    Log.e(LOG_TAG, "Verification email was not sent.")
                    val s: Snackbar? = Snackbar.make(activity?.findViewById(R.id.login_fragment_container)!!, getString(R.string.login_email_verification_failed), Snackbar.LENGTH_LONG).setDuration(resources.getInteger(R.integer.CUSTOM_SNACKBAR_LENGTH_LONG))
                    SystemUtils.setSnackbarDefaultOptions(s)
                    s?.setAction("YES") {
                        s.dismiss()
                        attemptEmailVerification(email, view)
                    }
                    s?.show()
                }
            }

        })
    }

    private fun isValidInput(email: String?, password: String?): Boolean {
        return !email.isNullOrEmpty() && !password.isNullOrEmpty() && email.length < 30 && password.length < 30 && email.contains('@') && email.contains('.')
    }

    private fun toggleLoginProgressBar(showProgress: Boolean) {
        attempt_login_button.visibility = if (showProgress) View.GONE else View.VISIBLE
        attempt_login_progress_bar.visibility = if (showProgress) View.VISIBLE else View.GONE
    }

    companion object {
        private val LOG_TAG: String = LoginFragment::class.java.name
    }
}
