package example.com.project306.util

import com.google.firebase.auth.FirebaseUser

data class SororityTimeSlot(
        val SororityName: String?,
        val StartTime: String?,
        val EndTime: String?
)

data class User(
        var FirebaseUser: FirebaseUser,
        var DisplayName: String?,
        var isEmailVerified: Boolean
)