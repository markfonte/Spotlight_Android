package example.com.project306.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import example.com.project306.R
import example.com.project306.adapter.RankingRecyclerAdapter
import example.com.project306.databinding.FragmentRankingBinding
import example.com.project306.util.InjectorUtils
import example.com.project306.util.RankingDatum
import kotlinx.android.synthetic.main.fragment_ranking.*

class RankingFragment : Fragment() {

    private lateinit var vm: RankingViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val factory: RankingViewModelFactory = InjectorUtils.provideRankingViewModelFactory()
        vm = ViewModelProviders.of(this, factory).get(RankingViewModel::class.java)
        val binding: FragmentRankingBinding = DataBindingUtil.inflate<FragmentRankingBinding>(inflater, R.layout.fragment_ranking, container, false).apply {
            viewModel = vm
            lifecycleOwner = this@RankingFragment
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildRankingView()
    }

    override fun onResume() {
        super.onResume()
        if ((activity as MainActivity).validateUser()) { //they are logged in
            vm.areValuesSet().observe(this, Observer {
                if (it == false) {
                    (activity as MainActivity).navController.navigate(R.id.action_rankingFragment_to_onboardingFragment, null)
                    vm.setBottomNavVisibility(false)
                    vm.setAppBarVisibility(false)
                } else {
                    vm.setBottomNavVisibility(true)
                    vm.setAppBarVisibility(true)
                }
            })
        }
    }

    private fun buildRankingView() {
        ranking_recycler_view.layoutManager = LinearLayoutManager(activity)
        if (vm.staticHouseData.value != null) {
            vm.getCurrentRanking().observe(this, Observer { taskResult ->
                if (taskResult != null) {
                    val staticHouseData = vm.staticHouseData.value as HashMap<String, HashMap<String, String>>
                    val currentRanking: HashMap<String, Int> = taskResult
                    val rankingData: ArrayList<RankingDatum> = arrayListOf()

                    var i = 0
                    for ((rankingKey, rankingValue) in currentRanking) {
                        val currentStaticHouseDatum = staticHouseData[rankingKey]
                        val currentRankingDatum = RankingDatum("", "", "", "", -2, "")
                        currentRankingDatum.HouseId = rankingKey
                        currentRankingDatum.CurrentRank = rankingValue
                        currentRankingDatum.DisplayName = currentStaticHouseDatum?.get("display_name")
                        currentRankingDatum.GreekLetters = currentStaticHouseDatum?.get("greek_letters")
                        currentRankingDatum.StreetAddress = currentStaticHouseDatum?.get("street_address")
                        currentRankingDatum.HouseIndex = i.toString() //Probably will want to delete this
                        rankingData.add(currentRankingDatum)
                        ++i
                    }
                    rankingData.sortBy { rankingDatum -> rankingDatum.CurrentRank }
                    val rankingAdapter = RankingRecyclerAdapter(rankingData, vm)
                    ranking_recycler_view.adapter = rankingAdapter
                    // For drag animation
                    val touchHelper = ItemTouchHelper(DragManageAdapter(rankingAdapter,
                            ItemTouchHelper.UP.or(ItemTouchHelper.DOWN),
                            ItemTouchHelper.ACTION_STATE_DRAG))
                    touchHelper.attachToRecyclerView(ranking_recycler_view)
                }
            })

        }
    }

    class DragManageAdapter(private val adapter: RankingRecyclerAdapter, dragDirs: Int, swipeDirs: Int) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            adapter.swapItems(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
    }

    companion object {
        private val LOG_TAG: String = RankingFragment::class.java.name
    }
}
