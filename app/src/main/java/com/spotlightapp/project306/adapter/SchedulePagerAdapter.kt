package com.spotlightapp.project306.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.spotlightapp.project306.ui.main.ScheduleFragment
import com.spotlightapp.project306.util.RoundTitles
import com.spotlightapp.project306.util.ScheduleDisplayMode

class SchedulePagerAdapter(fm: FragmentManager?, private val currentRound: Long?, private val scheduleExists: Boolean, private val bidHouse: String?) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        val fragment = ScheduleFragment()
        val displayMode: Int = if (bidHouse != "" && position == 3) {
            ScheduleDisplayMode().DISPLAY_BID
        } else if (!scheduleExists && bidHouse == "") { //displays the behind text if bid is present and are currently behind position 3
            ScheduleDisplayMode().DISPLAY_NO_SCHEDULES
        } else if (currentRound?.toInt() == position) {
            ScheduleDisplayMode().DISPLAY_CURRENT_SCHEDULE
        } else if (currentRound?.toInt()!! < position) {
            ScheduleDisplayMode().DISPLAY_AHEAD_OF_SCHEDULE
        } else {
            ScheduleDisplayMode().DISPLAY_BEHIND_SCHEDULE
        }
        fragment.arguments = Bundle().apply {
            putInt("SCHEDULE_PAGE_POSITION", position)
            putInt("SCHEDULE_DISPLAY_MODE", displayMode)
            putString("SCHEDULE_BID_HOUSE", bidHouse)
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