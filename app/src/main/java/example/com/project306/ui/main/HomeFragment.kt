package example.com.project306.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import example.com.project306.R
import example.com.project306.adapter.SchedulePagerAdapter
import example.com.project306.databinding.FragmentHomeBinding
import example.com.project306.util.InjectorUtils
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : androidx.fragment.app.Fragment() {

    private lateinit var homeFragmentViewModel: HomeViewModel
    private lateinit var scheduleViewPager: ViewPager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val factory: HomeViewModelFactory = InjectorUtils.provideHomeViewModelFactory()
        homeFragmentViewModel = ViewModelProviders.of(this, factory).get(HomeViewModel::class.java)
        val binding: FragmentHomeBinding = DataBindingUtil.inflate<FragmentHomeBinding>(inflater, R.layout.fragment_home, container, false).apply {
            viewModel = homeFragmentViewModel
            setLifecycleOwner(this@HomeFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeFragmentViewModel.staticHouseData.observe(this, Observer {
            //Schedule pages not inflated until static house data is acquired
            if (it != null) {
                homeFragmentViewModel.getScheduleData().observe(this, Observer { result ->
                    scheduleViewPager = schedule_view_pager
                    scheduleViewPager.adapter = SchedulePagerAdapter(childFragmentManager, result.first, result.second, result.third)
                    scheduleViewPager.currentItem = result.first?.toInt()!!
                    scheduleViewPager.offscreenPageLimit = 4
                    //scheduleViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tab_layout))
                    tab_layout.setupWithViewPager(scheduleViewPager, true)
                })

            }
        })
    }

    override fun onResume() {
        super.onResume()
        if ((activity as MainActivity).validateUser()) { //they are logged in
            homeFragmentViewModel.areValuesSet().observe(this, Observer {
                if (it == false) {
                    (activity as MainActivity).navController.navigate(R.id.action_homeFragment_to_onboardingFragment, null)
                    homeFragmentViewModel.setBottomNavVisibility(false)
                    homeFragmentViewModel.setAppBarVisibility(false)
                } else {
                    homeFragmentViewModel.setBottomNavVisibility(true)
                    homeFragmentViewModel.setAppBarVisibility(true)
                }
            })
        }
    }
}
