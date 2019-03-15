package com.spotlightapp.spotlight_android.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.StorageReference
import com.spotlightapp.spotlight_android.data.MainRepository
import java.util.*

class NotesViewModel(private val mainRepository: MainRepository) : ViewModel() {
    val displayName: MutableLiveData<String> = MutableLiveData()
    val greekLetters: MutableLiveData<String> = MutableLiveData()
    val streetAddress: MutableLiveData<String> = MutableLiveData()
    val houseIndex: MutableLiveData<String> = MutableLiveData()
    val houseId: MutableLiveData<String> = MutableLiveData()
    val valueOne: MutableLiveData<String> = MutableLiveData()
    val valueTwo: MutableLiveData<String> = MutableLiveData()
    val valueThree: MutableLiveData<String> = MutableLiveData()
    val isNoteLocked: MutableLiveData<Boolean> = MutableLiveData()
    val comments: MutableLiveData<String> = MutableLiveData()
    val isValueOneChecked: MutableLiveData<Boolean> = MutableLiveData()
    val isValueTwoChecked: MutableLiveData<Boolean> = MutableLiveData()
    val isValueThreeChecked: MutableLiveData<Boolean> = MutableLiveData()

    init {
        isNoteLocked.value = true
    }

    fun getUserValues(): MutableLiveData<ArrayList<String?>> {
        return mainRepository.getUserValues()
    }

    fun getStaticHouseImageReference(fileName: String): StorageReference {
        return mainRepository.getStaticHouseImageReference(fileName)
    }

    fun updateNoteInfo(houseId: String, comments: String, valueOne: Boolean, valueTwo: Boolean, valueThree: Boolean) {
        return mainRepository.updateNoteInfo(houseId, comments, valueOne, valueTwo, valueThree)
    }

    fun performDatabaseChangesForNoteSubmission(houseIndex: String, houseId: String, comments: String, valueOne: Boolean, valueTwo: Boolean, valueThree: Boolean): MutableLiveData<String> {
        return mainRepository.performDatabaseChangesForNoteSubmission(houseIndex, houseId, comments, valueOne, valueTwo, valueThree)
    }

    fun getNote(houseId: String): MutableLiveData<HashMap<String, Any>> {
        return mainRepository.getNote(houseId)
    }
}