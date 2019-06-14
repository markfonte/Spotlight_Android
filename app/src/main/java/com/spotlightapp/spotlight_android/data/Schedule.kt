package com.spotlightapp.spotlight_android.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * The Room representation of current_schedule from the deprecated
 * Firebase Firestore database.
 * Example:
 * current_schedule {
 *      1 -> {
 *          date: "1/12"
 *          house_id: "alpha_chi_omega"
 *          time: "5:00-5:25pm"
*       }
 *      2 -> {
 *          date: "1/12"
 *          house_id: "delta_delta_delta"
 *          time: "6:00-6:25pm"
 *      }
 *      4 -> {
 *          date: "1/12"
 *          house_id: "zeta_tau_alpha"
 *          time: "6:30-6:55pm"
 *      }
 * }
 */
@Entity(tableName = "schedule")
data class Schedule(
        @PrimaryKey @ColumnInfo(name = "id") val index: Int,
        val date: String,
        val houseId: String,
        val time: String
        ) {
    override fun toString() = "$houseId : $date : $time"
}