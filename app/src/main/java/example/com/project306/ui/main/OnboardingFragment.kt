package example.com.project306.ui.main

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.ContextCompat
import androidx.core.widget.CompoundButtonCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import example.com.project306.R
import example.com.project306.databinding.FragmentOnboardingBinding
import example.com.project306.util.InjectorUtils
import kotlinx.android.synthetic.main.fragment_onboarding.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class OnboardingFragment : Fragment() {
    private lateinit var onboardingFragmentViewModel: OnboardingViewModel
    private lateinit var panhelValues: ArrayList<*>
    private lateinit var currentlyCheckedBoxes: HashMap<Int, String>
    private lateinit var submittedCheckboxes: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        submittedCheckboxes = ArrayList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val factory: OnboardingViewModelFactory = InjectorUtils.provideOnboardingViewModelFactory()
        onboardingFragmentViewModel = ViewModelProviders.of(this, factory).get(OnboardingViewModel::class.java)
        val binding: FragmentOnboardingBinding = DataBindingUtil.inflate<FragmentOnboardingBinding>(inflater, R.layout.fragment_onboarding, container, false).apply {
            viewModel = onboardingFragmentViewModel
            setLifecycleOwner(this@OnboardingFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onboardingFragmentViewModel.panhelValues.observe(this, Observer {
            panhelValues = it
            currentlyCheckedBoxes = HashMap()
            var i = 0
            while (i < panhelValues.size) {
                currentlyCheckedBoxes[i] = ""
                ++i
            }
            buildRadioButtons()
        })
        onboarding_submit_button.setOnClickListener {
            var count = 0
            var i = 0
            submittedCheckboxes = ArrayList()
            while (i < panhelValues.size) {
                if (currentlyCheckedBoxes[i] != "") {
                    ++count
                    submittedCheckboxes.add(currentlyCheckedBoxes[i]!!)
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
        val alertDialog: AlertDialog = AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert)
                .setTitle("Invalid Submission")
                .setMessage("Please select exactly 3 values")
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(R.drawable.ic_warning_purple_24dp)
                .create()
        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                alertDialog.dismiss()
            }
        }
        alertDialog.show()
    }

    private fun sendConfirmationAlertDialog() {
        val alertDialog: AlertDialog = AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert)
                .setTitle("Confirm Your Selections")
                .setMessage("Select ${submittedCheckboxes[0]}, ${submittedCheckboxes[1]}, and ${submittedCheckboxes[2]}?\n\nYou will NOT be able to change them later!")
                .setPositiveButton(android.R.string.yes, null)
                .setNegativeButton(android.R.string.no, null)
                .create()
        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val newValuesMap: MutableMap<String, Any> = HashMap()
                newValuesMap["areValuesSet"] = true
                newValuesMap["values"] = Arrays.asList(submittedCheckboxes[0], submittedCheckboxes[1], submittedCheckboxes[2])
                onboardingFragmentViewModel.submitChosenValues(newValuesMap).observe(this, Observer { error ->
                    run {
                        if (error == "") {
                            val navOptions = NavOptions.Builder().setPopUpTo(R.id.homeFragment, true).build()
                            (activity as MainActivity).navController.navigate(R.id.action_onboardingFragment_to_homeFragment, null, navOptions)
                        } else {
                            Log.e(LOG_TAG, "Error setting values: $error")
                        }
                        alertDialog.dismiss()
                    }
                })
            }
        }
        alertDialog.show()
    }

    private fun buildRadioButtons() {
        val checkboxHolder = LinearLayout(context)
        checkboxHolder.orientation = LinearLayout.VERTICAL
        for ((counter, i) in panhelValues.withIndex()) {
            val newCheckbox = AppCompatCheckBox(context)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                newCheckbox.setTextAppearance(R.style.TextAppearance_AppCompat_Medium)
            }
            val paddingInPx = (4 * resources.displayMetrics.density + 0.5f).toInt()
            newCheckbox.setPadding(paddingInPx, 0, paddingInPx, 0)
            newCheckbox.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
            val colorStateList = ColorStateList(
                    arrayOf(intArrayOf(-android.R.attr.state_checked), // unchecked
                            intArrayOf(android.R.attr.state_checked)  // checked
                    ),
                    intArrayOf(ContextCompat.getColor(context!!, R.color.colorPrimary), ContextCompat.getColor(context!!, R.color.colorPrimary))
            )
            CompoundButtonCompat.setButtonTintList(newCheckbox, colorStateList)

            newCheckbox.id = counter
            newCheckbox.text = i.toString()
            newCheckbox.setOnClickListener {
                if ((it as CheckBox).isChecked) {
                    currentlyCheckedBoxes[it.id] = it.text.toString().trim()
                } else {
                    currentlyCheckedBoxes[it.id] = ""
                }
            }
            checkboxHolder.addView(newCheckbox)
        }
        onboarding_checkbox_holder.addView(checkboxHolder)
    }

    companion object {
        private val LOG_TAG: String = OnboardingFragment::class.java.name
    }
}
