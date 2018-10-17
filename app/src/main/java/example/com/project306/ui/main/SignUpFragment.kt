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
import example.com.project306.databinding.FragmentSignUpBinding
import example.com.project306.util.InjectorUtils
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
    }
}
