package example.com.project306.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import example.com.project306.R
import example.com.project306.util.SororityTimeSlot

class SororityScheduleRecyclerAdapter(private val sororityTimeSlots: MutableList<SororityTimeSlot>) : androidx.recyclerview.widget.RecyclerView.Adapter<SororityScheduleRecyclerAdapter.ViewHolder>() {

    class ViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
        val sororityName: TextView = v.findViewById(R.id.sorority_schedule_recycler_sorority_name)
        val timeSlot: TextView = v.findViewById(R.id.sorority_schedule_recycler_time_slot)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.main_fragment_recycler_row, parent, false))
    }

    override fun getItemCount(): Int = sororityTimeSlots.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.sororityName.text = sororityTimeSlots[position].HouseDisplayName
        val temp : String = sororityTimeSlots[position].Time!!
        viewHolder.timeSlot.text = temp

    }

}