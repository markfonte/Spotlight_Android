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
import example.com.project306.databinding.FragmentSettingsBinding
import example.com.project306.util.InjectorUtils
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment() {
    private lateinit var settingsFragmentViewModel: SettingsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val factory: SettingsViewModelFactory = InjectorUtils.provideSettingsViewModelFactory()
        settingsFragmentViewModel = ViewModelProviders.of(this, factory).get(SettingsViewModel::class.java)
        val binding = DataBindingUtil.inflate<FragmentSettingsBinding>(inflater, R.layout.fragment_settings, container, false).apply {
            viewModel = settingsFragmentViewModel
            setLifecycleOwner(this@SettingsFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settings_logout_button.setOnClickListener {
            settingsFragmentViewModel.logout().observe(this, Observer { logoutResult ->
                run {
                    if (logoutResult == "success") {
                        settingsFragmentViewModel.setBottomNavVisibility(false)
                        Navigation.findNavController(view).navigate(R.id.action_settingsFragment_to_loginStartFragment, null)
                    } else {
                        Log.e(LOG_TAG, "This should never happen. Fix IMMEDIATELY if this error occurs.")
                        activity?.finish()
                    }
                }

            })
        }
    }

    companion object {
        private val LOG_TAG: String = SettingsFragment::class.java.name
    }
}
