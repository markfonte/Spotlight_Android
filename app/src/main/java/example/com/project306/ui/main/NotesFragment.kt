package example.com.project306.ui.main

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
import example.com.project306.R
import example.com.project306.databinding.FragmentNotesBinding
import example.com.project306.util.GlideApp
import example.com.project306.util.InjectorUtils
import kotlinx.android.synthetic.main.fragment_notes.*


class NotesFragment : Fragment() {

    private lateinit var notesFragmentViewModel: NotesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val factory: NotesViewModelFactory = InjectorUtils.provideNotesViewModelFactory()
        notesFragmentViewModel = ViewModelProviders.of(this, factory).get(NotesViewModel::class.java)
        val binding: FragmentNotesBinding = DataBindingUtil.inflate<FragmentNotesBinding>(inflater, R.layout.fragment_notes, container, false).apply {
            viewModel = notesFragmentViewModel
            setLifecycleOwner(this@NotesFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notesFragmentViewModel.setBottomNavVisibility(false)
        notesFragmentViewModel.getUserValues().observe(this, Observer {
            if (it?.size == 3) {
                with(notesFragmentViewModel) {
                    valueOne.value = it[0]
                    valueTwo.value = it[1]
                    valueThree.value = it[2]
                }
            }
        })
        with(notesFragmentViewModel) {
            displayName.value = NotesFragmentArgs.fromBundle(arguments!!).displayName
            greekLetters.value = NotesFragmentArgs.fromBundle(arguments!!).greekLetters
            streetAddress.value = NotesFragmentArgs.fromBundle(arguments!!).streetAddress
            houseIndex.value = NotesFragmentArgs.fromBundle(arguments!!).houseIndex
            houseId.value = NotesFragmentArgs.fromBundle(arguments!!).houseId
            GlideApp.with(context!!)
                    .load(getStaticHouseImageReference(houseId.value!!))
                    .into(notes_house_image)
        }
        notes_submit_button.setOnClickListener {
            Log.d(LOG_TAG, "Save button clicked")
            showConfirmPopup()
        }
        notes_street_address.setOnClickListener {
            //documentation: https://developers.google.com/maps/documentation/urls/android-intents
            if (!notesFragmentViewModel.streetAddress.value.isNullOrEmpty()) {
                val gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(notesFragmentViewModel.streetAddress.value))
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                if (mapIntent.resolveActivity(activity?.packageManager!!) != null) {
                    (activity as MainActivity).startActivity(mapIntent)
                }
            }
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
                notesFragmentViewModel.performDatabaseChangesForNoteSubmission().observe(this, Observer { error ->
                    if (error == "") {
                        //TODO: Exit to Ranking Fragment
                        alertDialog.dismiss()
                    } else {
                        Log.e(LOG_TAG, "Error performing database changes for note submission. Fix immediately: $error")
                    }
                })

            }
        }
        alertDialog.show()
    }

    companion object {
        private val LOG_TAG: String = NotesFragment::class.java.name
    }
}
