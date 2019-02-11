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
import example.com.project306.util.RankingDatum

class RankingRecyclerAdapter(private val rankingData: ArrayList<RankingDatum>)  : androidx.recyclerview.widget.RecyclerView.Adapter<RankingRecyclerAdapter.ViewHolder>(){
    class ViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
        val displayName : TextView = v.findViewById(R.id.ranking_row_display_name)
        val greekLetters : TextView = v.findViewById(R.id.ranking_row_greek_letters)
        val houseId : TextView = v.findViewById(R.id.ranking_row_house_id)
        val houseIndex : TextView = v.findViewById(R.id.ranking_row_house_index)
        val currentRanking : TextView = v.findViewById(R.id.ranking_row_current_ranking)
        val streetAddress: TextView = v.findViewById(R.id.ranking_row_street_address)
        val rowContainer: ConstraintLayout = v.findViewById(R.id.ranking_row_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_ranking, parent, false))
    }

    override fun getItemCount(): Int = rankingData.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.displayName.text = rankingData[position].DisplayName
        viewHolder.greekLetters.text = rankingData[position].GreekLetters
        viewHolder.currentRanking.text = rankingData[position].CurrentRank.toString()
        viewHolder.houseId.text = rankingData[position].HouseId
        viewHolder.houseIndex.text = rankingData[position].HouseIndex
        viewHolder.streetAddress.text = rankingData[position].StreetAddress
        viewHolder.rowContainer.setOnClickListener {
            val displayName: String = it.findViewById<TextView>(R.id.ranking_row_display_name).text.toString()
            val greekLetters: String = it.findViewById<TextView>(R.id.ranking_row_greek_letters).text.toString()
            val streetAddress: String = it.findViewById<TextView>(R.id.ranking_row_street_address).text.toString()
            val houseId: String = it.findViewById<TextView>(R.id.ranking_row_house_id).text.toString()
            val houseIndex: String = it.findViewById<TextView>(R.id.ranking_row_house_index).text.toString()
            val isNoteLocked = true
            val bundle: Bundle = bundleOf("display_name" to displayName, "greek_letters" to greekLetters, "street_address" to streetAddress, "house_id" to houseId, "house_index" to houseIndex, "is_note_locked" to isNoteLocked)
            Navigation.findNavController(it).navigate(R.id.notesFragment, bundle)
        }
    }
}