package com.spotlightapp.spotlight_android.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.spotlightapp.spotlight_android.R
import com.spotlightapp.spotlight_android.util.BA
import com.spotlightapp.spotlight_android.util.TimeSlot

class ScheduleRecyclerAdapter(private val timeSlots: ArrayList<TimeSlot>) : androidx.recyclerview.widget.RecyclerView.Adapter<ScheduleRecyclerAdapter.ViewHolder>() {

    class ViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
        val displayName: TextView = v.findViewById(R.id.schedule_row_display_name)
        val time: TextView = v.findViewById(R.id.schedule_row_time)
        val date: TextView = v.findViewById(R.id.schedule_row_date)
        val greekLetters: TextView = v.findViewById(R.id.schedule_row_greek_letters)
        val houseId: TextView = v.findViewById(R.id.schedule_row_house_id)
        val rowContainer: ConstraintLayout = v.findViewById(R.id.schedule_row_container)
        val houseIndex: TextView = v.findViewById(R.id.schedule_row_house_index)
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
        viewHolder.houseId.text = timeSlots[position].HouseId
        viewHolder.houseIndex.text = timeSlots[position].HouseIndex
        viewHolder.rowContainer.setOnClickListener {
            val houseId: String = it.findViewById<TextView>(R.id.schedule_row_house_id).text.toString()
            val houseIndex: String = it.findViewById<TextView>(R.id.schedule_row_house_index).text.toString()
            val isNoteLocked = false
            val bundle: Bundle = bundleOf("${BA.HouseId}" to houseId, "${BA.HouseIndex}" to houseIndex, "${BA.IsNoteLocked}" to isNoteLocked)
            val navOptions = NavOptions.Builder()
            navOptions.setEnterAnim(android.R.anim.fade_in)
            navOptions.setExitAnim(android.R.anim.fade_out)
            navOptions.setPopEnterAnim(android.R.anim.fade_in)
            navOptions.setPopExitAnim(android.R.anim.fade_out)
            Navigation.findNavController(it).navigate(R.id.notesFragment, bundle, navOptions.build())
        }
    }
}