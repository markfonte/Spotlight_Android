package com.spotlightapp.spotlight_android.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import com.spotlightapp.spotlight_android.R
import com.spotlightapp.spotlight_android.adapter.SchedulePagerAdapter
import com.spotlightapp.spotlight_android.databinding.FragmentHomeBinding
import com.spotlightapp.spotlight_android.util.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : androidx.fragment.app.Fragment() {

    private lateinit var vm: HomeViewModel
    private var bidScreen = false

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
                vm.getScheduleMetaData().observe(this, Observer { result ->
                    if (result.first == null || result.third == null) {
                        CrashlyticsHelper.logError(exception = null, logTag = LOG_TAG, functionName = "onViewCreated()", message = "error in getScheduleMetaData() task. logging out user.")
                        (activity as MainActivity).logout()
                    } else {
                        if (result.third!! != "") { //bid has been given out
                            displayBidScreen(result.third!!)
                        } else {
                            vm.scheduleViewPager = schedule_view_pager
                            vm.scheduleViewPager?.adapter = SchedulePagerAdapter(childFragmentManager, currentRound = result.first!!, scheduleExists = result.second)
                            vm.scheduleViewPager?.currentItem = result.first?.toInt()!!
                            vm.scheduleViewPager?.offscreenPageLimit = 4
                            tab_layout.setupWithViewPager(vm.scheduleViewPager, true)
                        }
                    }
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
                if (bidScreen) {
                    vm.setBottomNavVisibility(false)
                    vm.setAppBarVisibility(false)
                    return@Observer
                }
                vm.setBottomNavVisibility(true)
                vm.setAppBarVisibility(true)
                return@Observer
            }
            assert(userState == enumValueOf<UserState>(UserState.LoggedOut.toString()) || userState == enumValueOf<UserState>(UserState.EmailNotVerified.toString()))
        })
    }

    private fun displayBidScreen(houseId: String) {
        vm.staticHouseData.observe(this, Observer { staticHouseData ->
            vm.houseDisplayName.value = staticHouseData[houseId]?.get("${DC.display_name}")
            vm.houseGreekLetters.value = staticHouseData[houseId]?.get("${DC.greek_letters}")
            GlideApp.with(context!!)
                    .load(vm.getStaticHouseImageReference(houseId))
                    .into(bid_screen_house_image)
            vm.bidHouse.value = houseId
            bidScreen = true
            bid_screen_log_out_button.setOnClickListener {
                vm.logout().observe(this, Observer { logoutResult ->
                    run {
                        if (logoutResult == "") {
                            val navOptions = NavOptions.Builder().setPopUpTo(R.id.loginFragment, true).build()
                            (activity as? MainActivity)?.navController?.navigate(R.id.action_homeFragment_to_landingFragment, null, navOptions)
                        } else {
                            Log.e(LOG_TAG, "This should never happen. Fix IMMEDIATELY if this error occurs.")
                            activity?.finish()
                        }
                    }
                })
            }
            vm.setAppBarVisibility(false)
            vm.setBottomNavVisibility(false)
        })
    }

    companion object {
        private val LOG_TAG: String = HomeFragment::class.java.name
    }
}
