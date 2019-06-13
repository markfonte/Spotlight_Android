package com.spotlightapp.spotlight_android.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Notes(
        @PrimaryKey @ColumnInfo(name = "id") val houseId: String,
        val comments: String = "",
        val value1: Boolean = false,
        val value2: Boolean = false,
        val value3: Boolean = false
) {
    override fun toString() = houseId
}