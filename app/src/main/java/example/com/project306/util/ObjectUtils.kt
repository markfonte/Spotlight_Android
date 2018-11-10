package example.com.project306.util

import com.google.firebase.auth.FirebaseUser

data class SororityTimeSlot(
        val Time: String?,
        val HouseDisplayName: String?,
        val HouseGreekLetters: String?,
        val HouseStreetAddress: String?
)

data class User(
        var FirebaseUser: FirebaseUser,
        var DisplayName: String?,
        var IsEmailVerified: Boolean
)