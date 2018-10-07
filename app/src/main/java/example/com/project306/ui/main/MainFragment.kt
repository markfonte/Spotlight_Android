package example.com.project306.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import example.com.project306.R
import example.com.project306.databinding.MainFragmentBinding
import example.com.project306.util.InjectorUtils
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : androidx.fragment.app.Fragment() {

    private lateinit var mainFragmentViewModel: MainViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val factory: MainViewModelFactory = InjectorUtils.provideMainViewModelFactory()
        mainFragmentViewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
        val binding = DataBindingUtil.inflate<MainFragmentBinding>(inflater, R.layout.main_fragment, container, false).apply {
            viewModel = mainFragmentViewModel
            setLifecycleOwner(this@MainFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        main_button.setOnClickListener {
            with(mainFragmentViewModel) {
                mDisplayName.value = "Mark Fonte"
            }
        }
        if(mainFragmentViewModel.firebaseService.getCurrentUser() == null) {
            view.let { Navigation.findNavController(it).navigate(R.id.action_mainFragment_to_loginFragment, null) }
        }
    }

}
