package example.com.project306.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import example.com.project306.R
import example.com.project306.adapter.ScheduleRecyclerAdapter
import example.com.project306.databinding.FragmentScheduleBinding
import example.com.project306.util.InjectorUtils
import example.com.project306.util.ScheduleDisplayMode
import example.com.project306.util.TimeSlot
import kotlinx.android.synthetic.main.fragment_schedule.*


class ScheduleFragment : Fragment() {

    private lateinit var vm: ScheduleViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val factory: ScheduleViewModelFactory = InjectorUtils.provideScheduleViewModelFactory()
        vm = ViewModelProviders.of(this, factory).get(ScheduleViewModel::class.java)
        val binding: FragmentScheduleBinding = DataBindingUtil.inflate<FragmentScheduleBinding>(inflater, R.layout.fragment_schedule, container, false).apply {
            viewModel = vm
            setLifecycleOwner(this@ScheduleFragment)
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
                        val currentStaticHouseData = staticHouseData[houseValue["house_id"]]
                        val currentTimeSlot = TimeSlot("", "", "", "", "", "", "")
                        currentTimeSlot.Time = houseValue["time"]
                        currentTimeSlot.Date = houseValue["date"]
                        currentTimeSlot.DisplayName = currentStaticHouseData?.get("display_name")
                        currentTimeSlot.GreekLetters = currentStaticHouseData?.get("greek_letters")
                        currentTimeSlot.StreetAddress = currentStaticHouseData?.get("street_address")
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