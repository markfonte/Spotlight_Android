package com.spotlightapp.spotlight_android.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.spotlightapp.spotlight_android.R
import com.spotlightapp.spotlight_android.adapter.SchedulePagerAdapter
import com.spotlightapp.spotlight_android.databinding.FragmentHomeBinding
import com.spotlightapp.spotlight_android.util.InjectorUtils
import com.spotlightapp.spotlight_android.util.UserState
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : androidx.fragment.app.Fragment() {

    private lateinit var vm: HomeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val factory: HomeViewModelFactory = InjectorUtils.provideHomeViewModelFactory()
        vm = ViewModelProviders.of(this, factory).get(HomeViewModel::class.java)
        val binding: FragmentHomeBinding = DataBindingUtil.inflate<FragmentHomeBinding>(inflater, R.layout.fragment_home, container, false).apply {
            viewModel = vm
            lifecycleOwner = this@HomeFragment
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.staticHouseData.observe(this, Observer {
            //Schedule pages not inflated until static house data is acquired
            if (it != null) {
                vm.getScheduleData().observe(this, Observer { result ->
                    vm.scheduleViewPager = schedule_view_pager
                    vm.scheduleViewPager?.adapter = SchedulePagerAdapter(childFragmentManager, result.first, result.second, result.third)
                    vm.scheduleViewPager?.currentItem = result.first?.toInt()!!
                    vm.scheduleViewPager?.offscreenPageLimit = 4
                    tab_layout.setupWithViewPager(vm.scheduleViewPager, true)
                })
            }
        })
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).validateUser().observe(this, Observer { userState ->
            if (userState == enumValueOf<UserState>(UserState.ValuesNotSet.toString())) {
                (activity as MainActivity).navController.navigate(R.id.action_homeFragment_to_onboardingFragment, null)
                vm.setBottomNavVisibility(false)
                vm.setAppBarVisibility(false)
                return@Observer
            }
            if (userState == enumValueOf<UserState>(UserState.LoggedIn.toString())) {
                vm.setBottomNavVisibility(true)
                vm.setAppBarVisibility(true)
                return@Observer
            }
            assert(userState == enumValueOf<UserState>(UserState.LoggedOut.toString()) || userState == enumValueOf<UserState>(UserState.EmailNotVerified.toString()))
        })
    }
}
