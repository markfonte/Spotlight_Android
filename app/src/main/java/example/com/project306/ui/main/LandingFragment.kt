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
import example.com.project306.databinding.FragmentLandingBinding
import example.com.project306.util.InjectorUtils
import kotlinx.android.synthetic.main.fragment_landing.*

class LandingFragment : Fragment() {

    private lateinit var vm: LandingViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val factory: LandingViewModelFactory = InjectorUtils.provideLandingViewModelFactory()
        vm = ViewModelProviders.of(this, factory).get(LandingViewModel::class.java)
        val binding: FragmentLandingBinding = DataBindingUtil.inflate<FragmentLandingBinding>(inflater, R.layout.fragment_landing, container, false).apply {
            viewModel = vm
            lifecycleOwner = this@LandingFragment
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        login_start_login_button.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_landingFragment_to_loginFragment, null)
        }
        login_start_sign_up_button.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_landingFragment_to_signUpFragment, null)
        }
    }

    companion object {
        private val LOG_TAG: String = LandingFragment::class.java.name
    }
}