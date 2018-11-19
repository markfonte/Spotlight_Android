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
import example.com.project306.adapter.SororitySchedulePagerAdapter
import example.com.project306.databinding.MainFragmentBinding
import example.com.project306.util.InjectorUtils
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : androidx.fragment.app.Fragment() {

    private lateinit var mainFragmentViewModel: MainViewModel
    private lateinit var sororitySchedulePagerAdapter: SororitySchedulePagerAdapter
    private lateinit var sororityScheduleViewPager: ViewPager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val factory: MainViewModelFactory = InjectorUtils.provideMainViewModelFactory()
        mainFragmentViewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
        val binding = DataBindingUtil.inflate<MainFragmentBinding>(inflater, R.layout.main_fragment, container, false).apply {
            viewModel = mainFragmentViewModel
            setLifecycleOwner(this@MainFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sororitySchedulePagerAdapter = SororitySchedulePagerAdapter(activity?.supportFragmentManager)
        sororityScheduleViewPager = sorority_schedule_view_pager
        sororityScheduleViewPager.adapter = sororitySchedulePagerAdapter
        tabLayout.setupWithViewPager(sororityScheduleViewPager)
    }

    override fun onResume() {
        super.onResume()
        if ((activity as MainActivity).validateUser()) { //they are logged in
            mainFragmentViewModel.areValuesSet().observe(this, Observer {
                if (it == false) {
                    (activity as MainActivity).navController.navigate(R.id.action_mainFragment_to_chooseValuesFragment, null)
                }
            })
        }
    }
}
