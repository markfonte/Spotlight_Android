package example.com.project306.util

import com.google.firebase.auth.FirebaseUser

data class SororityTimeSlot(
        var Time: String?,
        var Date: String?,
        var DisplayName: String?,
        var GreekLetters: String?,
        var StreetAddress: String?
)

data class User(
        var FirebaseUser: FirebaseUser,
        var DisplayName: String?,
        var IsEmailVerified: Boolean
)