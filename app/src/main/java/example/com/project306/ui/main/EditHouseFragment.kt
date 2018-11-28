package example.com.project306.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import example.com.project306.R
import example.com.project306.databinding.FragmentEditHouseBinding
import example.com.project306.util.InjectorUtils


class EditHouseFragment : Fragment() {

    private lateinit var editHouseFragmentViewModel: EditHouseViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val factory: EditHouseViewModelFactory = InjectorUtils.provideEditHouseViewModelFactory()
        editHouseFragmentViewModel = ViewModelProviders.of(this, factory).get(EditHouseViewModel::class.java)
        val binding = DataBindingUtil.inflate<FragmentEditHouseBinding>(inflater, R.layout.fragment_edit_house, container, false).apply {
            viewModel = editHouseFragmentViewModel
            setLifecycleOwner(this@EditHouseFragment)
        }
        return binding.root
    }

    companion object {
        private val LOG_TAG: String = EditHouseFragment::class.java.name
    }
}
