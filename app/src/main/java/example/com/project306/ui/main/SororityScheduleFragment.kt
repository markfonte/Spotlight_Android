package example.com.project306.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import example.com.project306.R
import example.com.project306.databinding.FragmentSororityScheduleBinding
import example.com.project306.util.InjectorUtils

class SororityScheduleFragment : Fragment() {

    private lateinit var sororityScheduleFragmentViewModel: SororityScheduleViewModel
    private var position : Int = -1
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

    }
}