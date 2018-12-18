package example.com.project306.util

data class TimeSlot(
        var Time: String?,
        var Date: String?,
        var DisplayName: String?,
        var GreekLetters: String?,
        var StreetAddress: String?,
        var HouseId: String?
)

data class RoundTitles(
        val FIRST_ROUND_DISPLAY_TITLE: String = "Open House Round",
        val SECOND_ROUND_DISPLAY_TITLE: String = "Philanthropy Round",
        val THIRD_ROUND_DISPLAY_TITLE: String = "Sisterhood Round",
        val FOURTH_ROUND_DISPLAY_TITLE: String = "Preference Round"
)