package example.com.project306.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import example.com.project306.ui.main.ScheduleFragment
import example.com.project306.util.RoundTitles
import example.com.project306.util.ScheduleDisplayMode

class SchedulePagerAdapter(fm: FragmentManager?, private val currentRound: Long?, private val scheduleExists: Boolean) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        val fragment = ScheduleFragment()
        val displayMode: Int = if (currentRound?.toInt() == position) {
            ScheduleDisplayMode().DISPLAY_CURRENT_SCHEDULE
        } else if (!scheduleExists) {
            ScheduleDisplayMode().DISPLAY_NO_SCHEDULES
        } else if (currentRound?.toInt()!! < position && scheduleExists) {
            ScheduleDisplayMode().DISPLAY_AHEAD_OF_SCHEDULE
        } else if (currentRound.toInt() > position && scheduleExists) {
            ScheduleDisplayMode().DISPLAY_BEHIND_SCHEDULE
        } else {
            ScheduleDisplayMode().DISPLAY_BID
        }
        fragment.arguments = Bundle().apply {
            putInt("SCHEDULE_PAGE_POSITION", position)
            putInt("SCHEDULE_DISPLAY_MODE", displayMode)
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