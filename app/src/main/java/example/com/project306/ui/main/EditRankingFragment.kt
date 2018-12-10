package example.com.project306.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import example.com.project306.R
import example.com.project306.databinding.FragmentEditRankingBinding
import example.com.project306.util.InjectorUtils

class EditRankingFragment : Fragment() {

    private lateinit var editRankingFragmentViewModel: EditRankingViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val factory: EditRankingViewModelFactory = InjectorUtils.provideEditRankingViewModelFactory()
        editRankingFragmentViewModel = ViewModelProviders.of(this, factory).get(EditRankingViewModel::class.java)
        val binding = DataBindingUtil.inflate<FragmentEditRankingBinding>(inflater, R.layout.fragment_edit_ranking, container, false).apply {
            viewModel = editRankingFragmentViewModel
            setLifecycleOwner(this@EditRankingFragment)
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if ((activity as MainActivity).validateUser()) { //they are logged in
            editRankingFragmentViewModel.areValuesSet().observe(this, Observer {
                if (it == false) {
                    (activity as MainActivity).navController.navigate(R.id.action_editRankingFragment_to_chooseValuesFragment, null)
                } else {
                    editRankingFragmentViewModel.setBottomNavVisibility(true)
                }
            })
        }
    }

    companion object {
        private val LOG_TAG: String = EditRankingFragment::class.java.name
    }
}
