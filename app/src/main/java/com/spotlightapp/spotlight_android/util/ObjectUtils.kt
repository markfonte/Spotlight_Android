package com.spotlightapp.spotlight_android.util

data class TimeSlot(
        var Time: String?,
        var Date: String?,
        var DisplayName: String?,
        var GreekLetters: String?,
        var StreetAddress: String?,
        var HouseId: String?,
        var HouseIndex: String?
)

data class RankingDatum(
        var DisplayName: String?,
        var GreekLetters: String?,
        var HouseIndex: String?,
        var HouseId: String?,
        var CurrentRank: Int?,
        var StreetAddress: String?
)

data class RoundTitles(
        val FIRST_ROUND_DISPLAY_TITLE: String = "Open House Round",
        val SECOND_ROUND_DISPLAY_TITLE: String = "Philanthropy Round",
        val THIRD_ROUND_DISPLAY_TITLE: String = "Sisterhood Round",
        val FOURTH_ROUND_DISPLAY_TITLE: String = "Preference Round"
)

data class ScheduleDisplayMode(
        val DISPLAY_CURRENT_SCHEDULE: Int = 0,
        val DISPLAY_AHEAD_OF_SCHEDULE: Int = 1,
        val DISPLAY_BEHIND_SCHEDULE: Int = 2,
        val DISPLAY_BID: Int = 3,
        val DISPLAY_NO_SCHEDULES: Int = 4
)