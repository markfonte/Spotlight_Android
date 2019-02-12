package example.com.project306.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import example.com.project306.R
import example.com.project306.ui.main.RankingViewModel
import example.com.project306.util.RankingDatum

class RankingRecyclerAdapter(private val rankingData: ArrayList<RankingDatum>, private val vm: RankingViewModel, private val context: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<RankingRecyclerAdapter.ViewHolder>() {
    class ViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
        val displayName: TextView = v.findViewById(R.id.ranking_row_display_name)
        val greekLetters: TextView = v.findViewById(R.id.ranking_row_greek_letters)
        val houseId: TextView = v.findViewById(R.id.ranking_row_house_id)
        val houseIndex: TextView = v.findViewById(R.id.ranking_row_house_index)
        val currentRanking: TextView = v.findViewById(R.id.ranking_row_current_ranking)
        val streetAddress: TextView = v.findViewById(R.id.ranking_row_street_address)
        val rowContainer: ConstraintLayout = v.findViewById(R.id.ranking_row_container)
        val buttonContainer: LinearLayout = v.findViewById(R.id.ranking_button_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_ranking, parent, false))
    }

    override fun getItemCount(): Int = rankingData.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.displayName.text = rankingData[position].DisplayName
        viewHolder.greekLetters.text = rankingData[position].GreekLetters
        viewHolder.houseId.text = rankingData[position].HouseId
        viewHolder.houseIndex.text = rankingData[position].HouseIndex
        viewHolder.streetAddress.text = rankingData[position].StreetAddress
        viewHolder.currentRanking.text = rankingData[position].CurrentRank.toString()
        if (rankingData[position].CurrentRank!! < 0) {
            viewHolder.currentRanking.visibility = View.INVISIBLE
            viewHolder.buttonContainer.background = ContextCompat.getDrawable(context, R.drawable.background_recycler_row_tinted)
        } else {
            viewHolder.currentRanking.visibility = View.VISIBLE
            viewHolder.buttonContainer.background = ContextCompat.getDrawable(context, R.drawable.background_recycler_row)
        }
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

    fun swapItems(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) { //going down
            for (i in fromPosition until toPosition) {
                rankingData[i] = rankingData.set(i + 1, rankingData[i])
            }
        } else { //going up
            for (i in fromPosition until toPosition + 1) {
                rankingData[i] = rankingData.set(i - 1, rankingData[i])
            }
        }
        recalculateRanking()
        saveToDatabase()
        notifyItemMoved(fromPosition, toPosition)
    }

    private fun saveToDatabase() {
        val updatedRanking = HashMap<String, Int>()
        for (i in rankingData) {
            updatedRanking[i.HouseId!!] = i.CurrentRank!!
        }
        vm.updateRanking(updatedRanking)
    }

    private fun recalculateRanking() {
        var index = 1
        var isInRankedSection = false
        for (i in rankingData) {
            if (i.CurrentRank!! < 0 && !isInRankedSection) {
                continue
            }
            isInRankedSection = true
            i.CurrentRank = index
            ++index
        }
    }

    fun draggingFinished() {
        notifyDataSetChanged()
    }
}