package com.spotlightapp.spotlight_android.ui.main

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import com.spotlightapp.spotlight_android.R
import com.spotlightapp.spotlight_android.databinding.FragmentNotesBinding
import com.spotlightapp.spotlight_android.util.*
import kotlinx.android.synthetic.main.fragment_notes.*


class NotesFragment : Fragment() {

    private lateinit var vm: NotesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val factory: NotesViewModelFactory = InjectorUtils.provideNotesViewModelFactory()
        vm = ViewModelProviders.of(this, factory).get(NotesViewModel::class.java)
        val binding: FragmentNotesBinding = DataBindingUtil.inflate<FragmentNotesBinding>(inflater, R.layout.fragment_notes, container, false).apply {
            viewModel = vm
            lifecycleOwner = this@NotesFragment
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getArgumentsFromBundle()
        loadHouseImage()
        getNoteInfo()

        notes_street_address.setOnClickListener {
            launchGoogleMapsActivity()
        }
        vm.isNoteLocked.value?.let { isNoteLocked ->
            if (!isNoteLocked) {
                notes_submit_button.setOnClickListener {
                    showConfirmPopup()
                }
            }
        }
    }

    // So their progress is saved if they accidentally navigate away
    override fun onPause() {
        super.onPause()
        vm.isNoteLocked.value?.let { isNoteLocked ->
            if (!isNoteLocked) {
                vm.houseId.value?.let { houseId ->
                    vm.updateNoteInfo(houseId, notes_enter_comments.text.toString(), notes_value_one.isChecked, notes_value_two.isChecked, notes_value_three.isChecked)
                }
            }
        }
    }

    private fun loadHouseImage() {
        vm.houseId.value?.let { houseId ->
            GlideApp.with(context!!)
                    .load(vm.getStaticHouseImageReference(houseId))
                    .into(notes_house_image)
        }
    }

    private fun launchGoogleMapsActivity() {
        //documentation: https://developers.google.com/maps/documentation/urls/android-intents
        if (!vm.streetAddress.value.isNullOrEmpty()) {
            val gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(vm.streetAddress.value))
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            if (mapIntent.resolveActivity(activity?.packageManager!!) != null) {
                (activity as MainActivity).startActivity(mapIntent)
            }
        }
    }

    private fun getArgumentsFromBundle() {
        with(vm) {
            arguments?.let {
                houseId.value = NotesFragmentArgs.fromBundle(it).houseId
                houseIndex.value = NotesFragmentArgs.fromBundle(it).houseIndex
                isNoteLocked.value = NotesFragmentArgs.fromBundle(it).isNoteLocked
            }
            if (houseId.value == null || houseIndex.value == null || isNoteLocked.value == null) {
                CrashlyticsHelper.logError(null, logTag = LOG_TAG, functionName = "getArgumentsFromBundle()", message = "error retrieving arguments from bundle. exiting notes fragment.")
                val navOptions = NavOptions.Builder().setPopUpTo(R.id.homeFragment, false).build()
                (activity as MainActivity).navController.navigate(R.id.action_notesFragment_to_homeFragment, null, navOptions)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getNoteInfo() {
        vm.houseId.value?.let { houseId ->
            vm.staticHouseData.observe(this, Observer { staticHouseData ->
                vm.getNote(houseId).observe(this, Observer { taskResult ->
                    taskResult?.let {
                        vm.valueOne.value = (taskResult["${DC.values}"] as? ArrayList<*>)?.get(0) as? String
                        vm.valueTwo.value = (taskResult["${DC.values}"] as? ArrayList<*>)?.get(1) as? String
                        vm.valueThree.value = (taskResult["${DC.values}"] as? ArrayList<*>)?.get(2) as? String
                        vm.comments.value = (taskResult["${DC.notes}"] as? HashMap<String, HashMap<String, Any>>)?.get(houseId)?.get("${DC.comments}") as? String
                        vm.isValueOneChecked.value = (taskResult["${DC.notes}"] as? HashMap<String, HashMap<String, Any>>)?.get(houseId)?.get("${DC.value1}") as? Boolean
                        vm.isValueTwoChecked.value = (taskResult["${DC.notes}"] as? HashMap<String, HashMap<String, Any>>)?.get(houseId)?.get("${DC.value2}") as? Boolean
                        vm.isValueThreeChecked.value = (taskResult["${DC.notes}"] as? HashMap<String, HashMap<String, Any>>)?.get(houseId)?.get("${DC.value3}") as? Boolean
                        vm.streetAddress.value = staticHouseData?.get(houseId)?.get("${DC.street_address}")
                        vm.displayName.value = staticHouseData?.get(houseId)?.get("${DC.display_name}")
                        vm.greekLetters.value = staticHouseData?.get(houseId)?.get("${DC.greek_letters}")
                    }
                })
            })
        }
    }

    private fun showConfirmPopup() {
        val alertDialog: AlertDialog = AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert)
                .setTitle("Are you sure you want to save your comments?")
                .setPositiveButton("Yes", null)
                .setNegativeButton("No", null)
                .create()
        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (!vm.isNoteLocked.value!!) { // Sanity check at worst
                    vm.performDatabaseChangesForNoteSubmission(vm.houseIndex.value!!, vm.houseId.value!!, notes_enter_comments.text.toString(), notes_value_one.isChecked, notes_value_two.isChecked, notes_value_three.isChecked).observe(this, Observer { error ->
                        if (error == "") {
                            val navOptions = NavOptions.Builder().setPopUpTo(R.id.homeFragment, false).build()
                            (activity as MainActivity).navController.navigate(R.id.action_notesFragment_to_rankingFragment, null, navOptions)
                            SystemUtils.hideKeyboardForced(context, it)
                            alertDialog.dismiss()
                        } else {
                            Log.e(LOG_TAG, "Error performing database changes for note submission. Fix immediately: $error")
                        }
                    })
                }
            }
        }
        alertDialog.show()
    }

    companion object {
        private val LOG_TAG: String = NotesFragment::class.java.name
    }
}
