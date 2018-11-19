package example.com.project306.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import example.com.project306.R
import example.com.project306.util.SororityTimeSlot

class SororityScheduleRecyclerAdapter(private val sororityTimeSlots: ArrayList<SororityTimeSlot>) : androidx.recyclerview.widget.RecyclerView.Adapter<SororityScheduleRecyclerAdapter.ViewHolder>() {

    class ViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
        val displayName: TextView = v.findViewById(R.id.sorority_schedule_row_display_name)
        val time: TextView = v.findViewById(R.id.sorority_schedule_row_time)
        val date: TextView = v.findViewById(R.id.sorority_schedule_row_date)
        val greekLetters: TextView = v.findViewById(R.id.sorority_schedule_row_greek_letters)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sorority_schedule_recycler_row, parent, false))
    }

    override fun getItemCount(): Int = sororityTimeSlots.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.displayName.text = sororityTimeSlots[position].DisplayName
        viewHolder.time.text = sororityTimeSlots[position].Time
        viewHolder.date.text = sororityTimeSlots[position].Date
        viewHolder.greekLetters.text = sororityTimeSlots[position].GreekLetters
    }
}