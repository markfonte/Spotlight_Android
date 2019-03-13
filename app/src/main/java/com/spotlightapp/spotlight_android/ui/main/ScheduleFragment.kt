package com.spotlightapp.spotlight_android.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.spotlightapp.spotlight_android.R
import com.spotlightapp.spotlight_android.adapter.ScheduleRecyclerAdapter
import com.spotlightapp.spotlight_android.databinding.FragmentScheduleBinding
import com.spotlightapp.spotlight_android.util.InjectorUtils
import com.spotlightapp.spotlight_android.util.ScheduleDisplayMode
import com.spotlightapp.spotlight_android.util.TimeSlot
import kotlinx.android.synthetic.main.fragment_schedule.*


class ScheduleFragment : Fragment() {

    private lateinit var vm: ScheduleViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val factory: ScheduleViewModelFactory = InjectorUtils.provideScheduleViewModelFactory()
        vm = ViewModelProviders.of(this, factory).get(ScheduleViewModel::class.java)
        val binding: FragmentScheduleBinding = DataBindingUtil.inflate<FragmentScheduleBinding>(inflater, R.layout.fragment_schedule, container, false).apply {
            viewModel = vm
            lifecycleOwner = this@ScheduleFragment
        }
        arguments?.takeIf { it.containsKey(activity?.getString(R.string.SCHEDULE_PAGE_POSITION)) }?.apply {
            vm.position = getInt(getString(R.string.SCHEDULE_PAGE_POSITION))
            vm.displayMode = getInt(getString(R.string.SCHEDULE_DISPLAY_MODE))
            vm.bidHouse = getString(getString(R.string.SCHEDULE_BID_HOUSE))
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildScheduleView()
    }

    private fun buildScheduleView() {
        if (vm.staticHouseData.value != null) {
            when (vm.displayMode) {
                ScheduleDisplayMode().DISPLAY_CURRENT_SCHEDULE -> vm.getCurrentSchedule().observe(this, Observer {
                    val houses: HashMap<String, HashMap<String, String>> = it
                    val timeSlots: ArrayList<TimeSlot> = arrayListOf()
                    val staticHouseData = vm.staticHouseData.value as HashMap<String, HashMap<String, String>>

                    for ((houseKey, houseValue) in houses) {
                        val currentStaticHouseDatum = staticHouseData[houseValue["house_id"]]
                        val currentTimeSlot = TimeSlot("", "", "", "", "", "", "")
                        currentTimeSlot.Time = houseValue["time"]
                        currentTimeSlot.Date = houseValue["date"]
                        currentTimeSlot.DisplayName = currentStaticHouseDatum?.get("display_name")
                        currentTimeSlot.GreekLetters = currentStaticHouseDatum?.get("greek_letters")
                        currentTimeSlot.StreetAddress = currentStaticHouseDatum?.get("street_address")
                        currentTimeSlot.HouseId = houseValue["house_id"]
                        currentTimeSlot.HouseIndex = houseKey
                        timeSlots.add(currentTimeSlot)
                        vm.isScheduleToDisplay.value = true
                    }
                    schedule_recycler_view.layoutManager = LinearLayoutManager(activity)
                    schedule_recycler_view.adapter = ScheduleRecyclerAdapter(timeSlots)
                })
                ScheduleDisplayMode().DISPLAY_AHEAD_OF_SCHEDULE -> {
                    vm.noScheduleMessage.value = getString(R.string.no_schedule_message_ahead)
                }
                ScheduleDisplayMode().DISPLAY_BEHIND_SCHEDULE -> {
                    vm.noScheduleMessage.value = getString(R.string.no_schedule_message_behind)
                }
                ScheduleDisplayMode().DISPLAY_BID -> {
                    vm.noScheduleMessage.value = "Congratulations! You got a bid from $vm.bidHouse!"
                }
                else -> {
                    assert(vm.displayMode == ScheduleDisplayMode().DISPLAY_NO_SCHEDULES)
                    vm.noScheduleMessage.value = getString(R.string.no_schedule_message_no_schedules)
                }
            }
            vm.isDataLoading.value = false
        }
    }

    companion object {
        private val LOG_TAG: String = ScheduleFragment::class.java.name
    }
}