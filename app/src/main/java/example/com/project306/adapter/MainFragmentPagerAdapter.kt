package example.com.project306.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import example.com.project306.ui.main.SororityScheduleFragment
import example.com.project306.util.RoundTitles

class SororitySchedulePagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        val fragment = SororityScheduleFragment()
        fragment.arguments = Bundle().apply {
            putInt( "SORORITY_SCHEDULE_PAGE_POSITION", position)
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