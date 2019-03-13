package com.spotlightapp.project306.ui.main

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import com.spotlightapp.project306.R
import com.spotlightapp.project306.databinding.FragmentNotesBinding
import com.spotlightapp.project306.util.GlideApp
import com.spotlightapp.project306.util.InjectorUtils
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
        vm.getUserValues().observe(this, Observer {
            if (it?.size == 3) {
                with(vm) {
                    valueOne.value = it[0]
                    valueTwo.value = it[1]
                    valueThree.value = it[2]
                }
            }
        })
        with(vm) {
            displayName.value = NotesFragmentArgs.fromBundle(arguments!!).displayName
            greekLetters.value = NotesFragmentArgs.fromBundle(arguments!!).greekLetters
            streetAddress.value = NotesFragmentArgs.fromBundle(arguments!!).streetAddress
            houseIndex.value = NotesFragmentArgs.fromBundle(arguments!!).houseIndex
            houseId.value = NotesFragmentArgs.fromBundle(arguments!!).houseId
            isNoteLocked.value = NotesFragmentArgs.fromBundle(arguments!!).isNoteLocked
            GlideApp.with(context!!)
                    .load(getStaticHouseImageReference(houseId.value!!))
                    .into(notes_house_image)
        }
        notes_street_address.setOnClickListener {
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
        getNoteInfo()
        if (vm.isNoteLocked.value!!) {

        } else {
            notes_submit_button.setOnClickListener {
                showConfirmPopup()
            }
        }

    }

    // So their progress is saved if they accidentally navigate away
    override fun onPause() {
        super.onPause()
        if (!vm.isNoteLocked.value!!) {
            vm.updateNoteInfo(vm.houseIndex.value!!, vm.houseId.value!!, notes_enter_comments.text.toString(), notes_value_one.isChecked, notes_value_two.isChecked, notes_value_three.isChecked)
        }
    }

    private fun getNoteInfo() {
        if (vm.houseId.value != null) {
            vm.getNote(vm.houseId.value!!).observe(this, Observer { taskResult ->
                if (taskResult != null) {
                    with(vm) {
                        comments.value = taskResult["comments"] as? String
                        isValueOneChecked.value = taskResult["value1"] as? Boolean
                        isValueTwoChecked.value = taskResult["value2"] as? Boolean
                        isValueThreeChecked.value = taskResult["value3"] as? Boolean
                    }
                }
            })
        }
    }

    private fun showConfirmPopup() {
        val alertDialog: AlertDialog = AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert)
                .setTitle("Are you sure you want to save your comments?")
                .setPositiveButton("Yes", null)
                .setNegativeButton("No", null)
                .create()
        alertDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (!vm.isNoteLocked.value!!) { // Sanity check at worst
                    vm.performDatabaseChangesForNoteSubmission(vm.houseIndex.value!!, vm.houseId.value!!, notes_enter_comments.text.toString(), notes_value_one.isChecked, notes_value_two.isChecked, notes_value_three.isChecked).observe(this, Observer { error ->
                        if (error == "") {
                            val navOptions = NavOptions.Builder().setPopUpTo(R.id.homeFragment, false).build()
                            (activity as MainActivity).navController.navigate(R.id.action_notesFragment_to_rankingFragment, null, navOptions)
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
