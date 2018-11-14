package example.com.project306.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import example.com.project306.R
import example.com.project306.databinding.FragmentChooseValuesBinding
import example.com.project306.util.InjectorUtils
import kotlinx.android.synthetic.main.fragment_choose_values.*

class ChooseValuesFragment : Fragment() {
    private lateinit var chooseValuesFragmentViewModel: ChooseValuesViewModel
    private lateinit var panhelValues: ArrayList<*>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val factory: ChooseValuesViewModelFactory = InjectorUtils.provideChooseValuesViewModelFactory()
        chooseValuesFragmentViewModel = ViewModelProviders.of(this, factory).get(ChooseValuesViewModel::class.java)
        val binding = DataBindingUtil.inflate<FragmentChooseValuesBinding>(inflater, R.layout.fragment_choose_values, container, false).apply {
            viewModel = chooseValuesFragmentViewModel
            setLifecycleOwner(this@ChooseValuesFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chooseValuesFragmentViewModel.panhelValues.observe(this, Observer {
            panhelValues = it
            buildRadioButtons()
        })
    }

    private fun buildRadioButtons() {
        val checkboxHolder = LinearLayout(context)
        checkboxHolder.orientation = LinearLayout.VERTICAL
        for ((counter, i) in panhelValues.withIndex()) {
            val newCheckbox = CheckBox(context)
            newCheckbox.id = counter
            newCheckbox.text = i.toString()
            checkboxHolder.addView(newCheckbox)
        }
        choose_values_checkbox_holder.addView(checkboxHolder)
    }
}
