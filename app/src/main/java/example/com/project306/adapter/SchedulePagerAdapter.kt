package example.com.project306.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import example.com.project306.ui.main.ScheduleFragment
import example.com.project306.util.RoundTitles

class SchedulePagerAdapter(fm: FragmentManager?, private val currentRound: Long?) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        val fragment = ScheduleFragment()
        val isCurrentSchedule = position == currentRound?.toInt()
        fragment.arguments = Bundle().apply {
            putInt("SCHEDULE_PAGE_POSITION", position)
            putBoolean("SCHEDULE_IS_CURRENT_SCHEDULE", isCurrentSchedule)
        }
        return fragment
    }

    override fun getCount(): Int {
        return 4
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> RoundTitles().FIRST_ROUND_DISPLAY_TITLE
            1 -> RoundTitles().SECOND_ROUND_DISPLAY_TITLE
            2 -> RoundTitles().THIRD_ROUND_DISPLAY_TITLE
            else -> RoundTitles().FOURTH_ROUND_DISPLAY_TITLE
        }
    }
}