package example.com.project306.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editHouseFragmentViewModel.getUserValues().observe(this, Observer {
            if (it.size == 3) {
                with(editHouseFragmentViewModel) {
                    valueOne.value = it[0]
                    valueTwo.value = it[1]
                    valueThree.value = it[2]
                }
            }
        })
        with(editHouseFragmentViewModel) {
            displayName.value = EditHouseFragmentArgs.fromBundle(arguments).displayName
            greekLetters.value = EditHouseFragmentArgs.fromBundle(arguments).greekLetters
            streetAddress.value = EditHouseFragmentArgs.fromBundle(arguments).streetAddress
        }
    }

    companion object {
        private val LOG_TAG: String = EditHouseFragment::class.java.name
    }
}
