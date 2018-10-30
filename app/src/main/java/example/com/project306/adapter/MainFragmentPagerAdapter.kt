package example.com.project306.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import example.com.project306.ui.main.SororityScheduleFragment

class SororitySchedulePagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        val fragment = SororityScheduleFragment()
        fragment.arguments = Bundle().apply {
            putInt( "SORORITY_SCHEDULE_PAGE_POSITION", position)
        }
        return fragment
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return super.getPageTitle(position)
    }
}