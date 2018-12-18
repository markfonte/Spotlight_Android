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
import example.com.project306.databinding.FragmentRankingBinding
import example.com.project306.util.InjectorUtils

class RankingFragment : Fragment() {

    private lateinit var rankingFragmentViewModel: RankingViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val factory: RankingViewModelFactory = InjectorUtils.provideRankingViewModelFactory()
        rankingFragmentViewModel = ViewModelProviders.of(this, factory).get(RankingViewModel::class.java)
        val binding: FragmentRankingBinding = DataBindingUtil.inflate<FragmentRankingBinding>(inflater, R.layout.fragment_ranking, container, false).apply {
            viewModel = rankingFragmentViewModel
            setLifecycleOwner(this@RankingFragment)
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if ((activity as MainActivity).validateUser()) { //they are logged in
            rankingFragmentViewModel.areValuesSet().observe(this, Observer {
                if (it == false) {
                    (activity as MainActivity).navController.navigate(R.id.action_rankingFragment_to_chooseValuesFragment, null)
                    rankingFragmentViewModel.setBottomNavVisibility(false)
                    rankingFragmentViewModel.setAppBarVisibility(false)
                } else {
                    rankingFragmentViewModel.setBottomNavVisibility(true)
                    rankingFragmentViewModel.setAppBarVisibility(true)
                }
            })
        }
    }

    companion object {
        private val LOG_TAG: String = RankingFragment::class.java.name
    }
}
