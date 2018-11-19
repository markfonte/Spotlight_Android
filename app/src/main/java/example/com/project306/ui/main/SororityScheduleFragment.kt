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
import example.com.project306.adapter.SororityScheduleRecyclerAdapter
import example.com.project306.databinding.FragmentSororityScheduleBinding
import example.com.project306.util.InjectorUtils
import example.com.project306.util.SororityTimeSlot
import kotlinx.android.synthetic.main.fragment_sorority_schedule.*

class SororityScheduleFragment : Fragment() {

    private lateinit var sororityScheduleFragmentViewModel: SororityScheduleViewModel
    private var position: Int = -1
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val factory: SororityScheduleViewModelFactory = InjectorUtils.provideSororityScheduleViewModelFactory()
        sororityScheduleFragmentViewModel = ViewModelProviders.of(this, factory).get(SororityScheduleViewModel::class.java)
        val binding = DataBindingUtil.inflate<FragmentSororityScheduleBinding>(inflater, R.layout.fragment_sorority_schedule, container, false).apply {
            viewModel = sororityScheduleFragmentViewModel
            setLifecycleOwner(this@SororityScheduleFragment)
        }
        arguments?.takeIf { it.containsKey(activity?.getString(R.string.SORORITY_SCHEDULE_PAGE_POSITION)) }?.apply {
            position = getInt(activity?.getString(R.string.SORORITY_SCHEDULE_PAGE_POSITION))
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildScheduleView()
    }

    private fun buildScheduleView() {
        val scheduleName: String = when (position) {
            0 -> "first_round"
            1 -> "second_round"
            2 -> "third_round"
            else -> "fourth_round"
        }
        sororityScheduleFragmentViewModel.getSchedule(scheduleName).observe(this, Observer {
            val houses: ArrayList<HashMap<String, String>> = it
            val timeSlots: ArrayList<SororityTimeSlot> = arrayListOf()
            for (house in houses) {
                house as HashMap<String, String>
                val currentTimeSlot = SororityTimeSlot("", "", "", "", "")
                currentTimeSlot.Time = house["time"]
                currentTimeSlot.Date = house["date"]
                currentTimeSlot.DisplayName = house["house_id"] //TODO: Change when pulling in "static" house information
                timeSlots.add(currentTimeSlot)
                sororityScheduleFragmentViewModel.isDataToDisplay.value = true
            }
            sorority_schedule_recycler_view.layoutManager = LinearLayoutManager(activity)
            sorority_schedule_recycler_view.adapter = SororityScheduleRecyclerAdapter(timeSlots)
        })

    }
}