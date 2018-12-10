package example.com.project306.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import example.com.project306.R
import example.com.project306.util.SororityTimeSlot

class SororityScheduleRecyclerAdapter(private val sororityTimeSlots: ArrayList<SororityTimeSlot>) : androidx.recyclerview.widget.RecyclerView.Adapter<SororityScheduleRecyclerAdapter.ViewHolder>() {

    class ViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
        val displayName: TextView = v.findViewById(R.id.sorority_schedule_row_display_name)
        val time: TextView = v.findViewById(R.id.sorority_schedule_row_time)
        val date: TextView = v.findViewById(R.id.sorority_schedule_row_date)
        val greekLetters: TextView = v.findViewById(R.id.sorority_schedule_row_greek_letters)
        val streetAddress: TextView = v.findViewById(R.id.sorority_schedule_row_street_address)
        val clickContainer: LinearLayout = v.findViewById(R.id.sorority_schedule_button_container)
        val rowContainer: ConstraintLayout = v.findViewById(R.id.sorority_schedule_row_container)
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
        viewHolder.streetAddress.text = sororityTimeSlots[position].StreetAddress
        viewHolder.rowContainer.setOnClickListener {
            val displayName : String = it.findViewById<TextView>(R.id.sorority_schedule_row_display_name).text.toString()
            val greekLetters : String = it.findViewById<TextView>(R.id.sorority_schedule_row_greek_letters).text.toString()
            val streetAddress: String = it.findViewById<TextView>(R.id.sorority_schedule_row_street_address).text.toString()
            val bundle: Bundle = bundleOf("display_name" to displayName, "greek_letters" to greekLetters, "street_address" to streetAddress)
            Navigation.findNavController(it).navigate(R.id.editHouseFragment, bundle)
        }
    }
}