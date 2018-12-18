package example.com.project306.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import example.com.project306.R
import example.com.project306.util.TimeSlot

class ScheduleRecyclerAdapter(private val timeSlots: ArrayList<TimeSlot>) : androidx.recyclerview.widget.RecyclerView.Adapter<ScheduleRecyclerAdapter.ViewHolder>() {

    class ViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
        val displayName: TextView = v.findViewById(R.id.schedule_row_display_name)
        val time: TextView = v.findViewById(R.id.schedule_row_time)
        val date: TextView = v.findViewById(R.id.schedule_row_date)
        val greekLetters: TextView = v.findViewById(R.id.schedule_row_greek_letters)
        val streetAddress: TextView = v.findViewById(R.id.schedule_row_street_address)
        val houseId: TextView = v.findViewById(R.id.schedule_row_house_id)
        val rowContainer: ConstraintLayout = v.findViewById(R.id.schedule_row_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_schedule, parent, false))
    }

    override fun getItemCount(): Int = timeSlots.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.displayName.text = timeSlots[position].DisplayName
        viewHolder.time.text = timeSlots[position].Time
        viewHolder.date.text = timeSlots[position].Date
        viewHolder.greekLetters.text = timeSlots[position].GreekLetters
        viewHolder.streetAddress.text = timeSlots[position].StreetAddress
        viewHolder.houseId.text = timeSlots[position].HouseId
        viewHolder.rowContainer.setOnClickListener {
            val displayName: String = it.findViewById<TextView>(R.id.schedule_row_display_name).text.toString()
            val greekLetters: String = it.findViewById<TextView>(R.id.schedule_row_greek_letters).text.toString()
            val streetAddress: String = it.findViewById<TextView>(R.id.schedule_row_street_address).text.toString()
            val houseId: String = it.findViewById<TextView>(R.id.schedule_row_house_id).text.toString()
            val bundle: Bundle = bundleOf("display_name" to displayName, "greek_letters" to greekLetters, "street_address" to streetAddress, "house_id" to houseId)
            Navigation.findNavController(it).navigate(R.id.notesFragment, bundle)
        }
    }
}