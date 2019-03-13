package com.spotlightapp.spotlight_android.util

import android.util.Log
import com.crashlytics.android.Crashlytics
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException

class CrashlyticsHelper {
    companion object {

        fun logDebug(logTag: String = "", functionName: String = "", message: String = "") {
            Crashlytics.log(Log.ERROR, logTag, "$functionName: Error in task - Message: $message")
        }

        fun logDebugTask(task: Task<*>, logTag: String = "", functionName: String = "", message: String = "") {
            Crashlytics.log(Log.DEBUG, logTag, "$functionName: Task result - Message: $message Result: ${task.result.toString()}; Exception(if any): ${task.exception.toString()}.")
        }

        fun logDebugSnapshot(snapshot: DocumentSnapshot?, logTag: String = "", functionName: String = "", message: String = "") {
            if (snapshot != null && snapshot.exists()) {
                Crashlytics.log(Log.DEBUG, logTag, "$functionName: Snapshot - Message: $message ResultL ${snapshot.data.toString()}")
            }
        }
        fun logError(exception: Exception? = null, logTag: String = "", functionName: String = "", message: String = "") {
            Crashlytics.log(Log.ERROR, logTag, "$functionName: Error in task - Message: $message Exception: ${exception.toString()}")
            Crashlytics.logException(exception)
        }

        fun logErrorTask(task: Task<*>, logTag: String = "", functionName: String = "", message: String = "") {
            Crashlytics.log(Log.ERROR, logTag, "$functionName: Error in task - Message: $message Exception: ${task.exception.toString()}.")
            Crashlytics.logException(task.exception)
        }

        fun logErrorSnapshot(exception: FirebaseFirestoreException?, snapshot: DocumentSnapshot?, logTag: String = "", functionName: String = "", message: String = "") {
            Crashlytics.log(Log.ERROR, logTag, "$functionName: Error in task - Message: $message Exception: ${exception?.toString()}.")
            Crashlytics.logException(exception)
        }

        fun setCrashlyticsUserIdentifier(identifier: String?) {
            Crashlytics.setUserIdentifier(identifier)
        }

        fun resetCrashlyticsUserIdentifier() {
            Crashlytics.setUserIdentifier("")
        }
    }
}