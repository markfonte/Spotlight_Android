package com.spotlightapp.spotlight_android.util

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

data class TimeSlot(
        var Time: String?,
        var Date: String?,
        var DisplayName: String?,
        var GreekLetters: String?,
        var StreetAddress: String?,
        var HouseId: String?,
        var HouseIndex: String?
)

enum class UserState {
    LoggedIn,
    EmailNotVerified,
    ValuesNotSet,
    LoggedOut
}

/*
    Database constants - for indexing into firebase database
 */
enum class DC {
    are_values_set,
    bid_house,
    comments,
    current_schedule,
    current_ranking,
    current_round,
    date,
    display_name,
    greek_letters,
    house_id,
    house_images,
    house_information,
    notes,
    panhel_data,
    panhel_values,
    street_address,
    time,
    users,
    value1,
    value2,
    value3,
    values
}

/*
    Bundle args - keys for bundle arguments
 */
enum class BA {
    ScheduleBidHouse,
    ScheduleDisplayMode,
    SchedulePagePosition,
    ScheduleCurrentRound,

    DisplayName,
    GreekLetters,
    HouseId,
    HouseIndex,
    IsNoteLocked,
    StreetAddress,
}