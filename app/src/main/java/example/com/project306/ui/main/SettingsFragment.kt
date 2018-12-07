package example.com.project306.ui.main

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import example.com.project306.R
import example.com.project306.databinding.FragmentSettingsBinding
import example.com.project306.util.InjectorUtils
import example.com.project306.util.SystemUtils
import kotlinx.android.synthetic.main.enter_name_dialog.*
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment() {
    private lateinit var settingsFragmentViewModel: SettingsViewModel
    private lateinit var temporaryDisplayName: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val factory: SettingsViewModelFactory = InjectorUtils.provideSettingsViewModelFactory()
        settingsFragmentViewModel = ViewModelProviders.of(this, factory).get(SettingsViewModel::class.java)
        val binding = DataBindingUtil.inflate<FragmentSettingsBinding>(inflater, R.layout.fragment_settings, container, false).apply {
            viewModel = settingsFragmentViewModel
            setLifecycleOwner(this@SettingsFragment)
        }
        return binding.root
    }

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settings_logout_button.setOnClickListener {
            settingsFragmentViewModel.logout().observe(this, Observer { logoutResult ->
                run {
                    if (logoutResult == "success") {
                        val navOptions = NavOptions.Builder().setPopUpTo(R.id.loginFragment, true).build()
                        Navigation.findNavController(view).navigate(R.id.action_settingsFragment_to_loginStartFragment, null, navOptions)
                    } else {
                        Log.e(LOG_TAG, "This should never happen. Fix IMMEDIATELY if this error occurs.")
                        activity?.finish()
                    }
                }
            })
        }
        settingsFragmentViewModel.getUserValues().observe(this, Observer {
            if (it.size == 3) {
                with(settingsFragmentViewModel) {
                    valueOne.value = it[0]
                    valueTwo.value = it[1]
                    valueThree.value = it[2]
                }
            }
        })
        display_user_data_display_name.setOnClickListener {
            val alertDialog: AlertDialog = AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert)
                    .setTitle("Change Your Name")
                    .setView(activity?.layoutInflater?.inflate(R.layout.enter_name_dialog, null))
                    .setPositiveButton("Change", null)
                    .setNegativeButton("Cancel", null)
                    .create()
            alertDialog.setOnShowListener {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    temporaryDisplayName = alertDialog.enter_name_dialog_enter_name.text.toString()
                    if (isValid(temporaryDisplayName)) {
                        settingsFragmentViewModel.changeDisplayName(temporaryDisplayName).observe(this, Observer { error ->
                            if (error == "") {
                                settingsFragmentViewModel.displayName.value = temporaryDisplayName
                                val s: Snackbar? = Snackbar.make(activity?.findViewById(R.id.settings_fragment_container)!!, "Success! Name changed to ${settingsFragmentViewModel.displayName.value}", Snackbar.LENGTH_LONG)
                                SystemUtils.setSnackbarDefaultOptions(s)
                                s?.show()
                            } else {
                                val s: Snackbar? = Snackbar.make(activity?.findViewById(R.id.settings_fragment_container)!!, "Your name change request encountered an error. Please check your connection and try again.", Snackbar.LENGTH_INDEFINITE)
                                SystemUtils.setSnackbarDefaultOptions(s)
                                s?.setAction("OK") {
                                    s.dismiss()
                                }
                                s?.show()
                            }
                        })
                        alertDialog.dismiss()
                    }
                }
                alertDialog.enter_name_dialog_enter_name.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        alertDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                    }
                }
                alertDialog.enter_name_dialog_enter_name.requestFocus()
                alertDialog.enter_name_dialog_enter_name.setText(settingsFragmentViewModel.displayName.value)
                settingsFragmentViewModel.displayName.value?.length?.let { length -> alertDialog.enter_name_dialog_enter_name.setSelection(length) }
            }
            alertDialog.show()
        }
    }

    private fun isValid(displayName: String?): Boolean {
        return !displayName.isNullOrEmpty() && displayName.length < 30 && displayName != settingsFragmentViewModel.displayName.value
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).validateUser()
    }

    companion object {
        private val LOG_TAG: String = SettingsFragment::class.java.name
    }
}
