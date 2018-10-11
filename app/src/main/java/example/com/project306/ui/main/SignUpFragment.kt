package example.com.project306.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import example.com.project306.R
import example.com.project306.databinding.FragmentSignUpBinding
import example.com.project306.util.InjectorUtils


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
}
