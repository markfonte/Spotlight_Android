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
import example.com.project306.util.TimeSlot
import kotlinx.android.synthetic.main.fragment_schedule.*


class ScheduleFragment : Fragment() {

    private lateinit var scheduleFragmentViewModel: ScheduleViewModel
    private var position: Int = -1
    private var isCurrentSchedule: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val factory: ScheduleViewModelFactory = InjectorUtils.provideScheduleViewModelFactory()
        scheduleFragmentViewModel = ViewModelProviders.of(this, factory).get(ScheduleViewModel::class.java)
        val binding: FragmentScheduleBinding = DataBindingUtil.inflate<FragmentScheduleBinding>(inflater, R.layout.fragment_schedule, container, false).apply {
            viewModel = scheduleFragmentViewModel
            setLifecycleOwner(this@ScheduleFragment)
        }
        arguments?.takeIf { it.containsKey(activity?.getString(R.string.SCHEDULE_PAGE_POSITION)) }?.apply {
            position = getInt(activity?.getString(R.string.SCHEDULE_PAGE_POSITION))
            isCurrentSchedule = getBoolean(activity?.getString(R.string.SCHEDULE_IS_CURRENT_SCHEDULE))
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildScheduleView()
    }

    private fun buildScheduleView() {
        if (scheduleFragmentViewModel.staticHouseData.value != null) {
            if (isCurrentSchedule) {
                scheduleFragmentViewModel.getSchedule("current_schedule").observe(this, Observer {
                    val houses: ArrayList<HashMap<String, String>> = it
                    val timeSlots: ArrayList<TimeSlot> = arrayListOf()
                    val staticHouseData = scheduleFragmentViewModel.staticHouseData.value as HashMap<String, HashMap<String, String>>
                    for (house in houses) {
                        val currentStaticHouseData = staticHouseData[house["house_id"]]
                        val currentTimeSlot = TimeSlot("", "", "", "", "", "")
                        currentTimeSlot.Time = house["time"]
                        currentTimeSlot.Date = house["date"]
                        currentTimeSlot.DisplayName = currentStaticHouseData?.get("display_name")
                        currentTimeSlot.GreekLetters = currentStaticHouseData?.get("greek_letters")
                        currentTimeSlot.StreetAddress = currentStaticHouseData?.get("street_address")
                        currentTimeSlot.HouseId = house["house_id"]
                        timeSlots.add(currentTimeSlot)
                        scheduleFragmentViewModel.isDataToDisplay.value = true
                    }
                    scheduleFragmentViewModel.isDataLoading.value = false
                    schedule_recycler_view.layoutManager = LinearLayoutManager(activity)
                    schedule_recycler_view.adapter = ScheduleRecyclerAdapter(timeSlots)
                })
            } else {
                //TODO: display other information
            }
        }
    }

    companion object {
        private val LOG_TAG: String = ScheduleFragment::class.java.name
    }
}