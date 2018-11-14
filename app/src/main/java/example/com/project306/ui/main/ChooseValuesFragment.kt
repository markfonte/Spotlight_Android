package example.com.project306.ui.main

import android.app.AlertDialog
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
import java.util.*
import kotlin.collections.HashMap


class ChooseValuesFragment : Fragment() {
    private lateinit var chooseValuesFragmentViewModel: ChooseValuesViewModel
    private lateinit var panhelValues: ArrayList<*>
    private lateinit var currentlyCheckedBoxes: HashMap<Int, String>
    private lateinit var submittedCheckboxes: Stack<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        submittedCheckboxes = Stack()
    }

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
            currentlyCheckedBoxes = HashMap()
            var i = 0
            while (i < panhelValues.size) {
                currentlyCheckedBoxes[i] = ""
                ++i
            }
            buildRadioButtons()
        })
        choose_values_submit_button.setOnClickListener {
            var count = 0
            var i = 0
            while (i < panhelValues.size) {
                if (currentlyCheckedBoxes[i] != "") {
                    ++count
                    submittedCheckboxes.push(currentlyCheckedBoxes[i])
                }
                ++i
            }
            if (count == 3) {
                sendConfirmationAlertDialog()
            } else {
                sendErrorAlertDialog()
            }
        }
    }

    private fun sendErrorAlertDialog() {
        val alertDialog: AlertDialog = AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert)
                .setTitle("Invalid Submission")
                .setMessage("Please select exactly 3 values")
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .create()
        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                alertDialog.dismiss()
            }
        }
        alertDialog.show()
    }

    private fun sendConfirmationAlertDialog() {
        val alertDialog: AlertDialog = AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert)
                .setTitle("Confirm Your Selections")
                .setMessage("Select ${submittedCheckboxes.pop()}, ${submittedCheckboxes.pop()}, and ${submittedCheckboxes.pop()}?")
                .setPositiveButton(android.R.string.yes, null)
                .setNegativeButton(android.R.string.no, null)
                .create()
        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                //send to database, release if successful
            }
        }
        alertDialog.show()
    }

    private fun buildRadioButtons() {
        val checkboxHolder = LinearLayout(context)
        checkboxHolder.orientation = LinearLayout.VERTICAL
        for ((counter, i) in panhelValues.withIndex()) {
            val newCheckbox = CheckBox(context)
            newCheckbox.id = counter
            newCheckbox.text = i.toString()
            newCheckbox.setOnClickListener {
                if ((it as CheckBox).isChecked) {
                    currentlyCheckedBoxes[it.id] = it.text.toString()
                } else {
                    currentlyCheckedBoxes[it.id] = ""
                }
            }
            checkboxHolder.addView(newCheckbox)
        }
        choose_values_checkbox_holder.addView(checkboxHolder)
    }
}
