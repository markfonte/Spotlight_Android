package com.spotlightapp.spotlight_android.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update

@Dao
interface NotesDao {
    @Query("SELECT * FROM notes WHERE houseId = :houseId ORDER BY houseId")
    fun getNote(houseId: String): LiveData<Notes>

    @Update
    fun updateNoteInfo(note: Notes)
}