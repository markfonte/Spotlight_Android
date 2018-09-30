package example.com.project306.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import example.com.project306.R
import example.com.project306.util.SororityTimeSlot

class HomeFragmentRecyclerAdapter(private val sororityTimeSlots: MutableList<SororityTimeSlot>) : androidx.recyclerview.widget.RecyclerView.Adapter<HomeFragmentRecyclerAdapter.ViewHolder>() {

    class ViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
        val sororityName: TextView = v.findViewById(R.id.home_fragment_recycler_sorority_name)
        val timeSlot: TextView = v.findViewById(R.id.home_fragment_recycler_time_slot)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.home_fragment_recycler_row, parent, false))
    }

    override fun getItemCount(): Int = sororityTimeSlots.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.sororityName.text = sororityTimeSlots[position].SororityName
        val temp : String = sororityTimeSlots[position].StartTime + " : " + sororityTimeSlots[position].EndTime
        viewHolder.timeSlot.text = temp
    }

}