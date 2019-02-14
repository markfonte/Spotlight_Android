package example.com.project306.util

import android.util.Log
import com.crashlytics.android.Crashlytics
import com.google.android.gms.tasks.Task

class CrashlyticsHelper {
    companion object {
        fun logErrorTask(task: Task<*>, logTag: String = "", functionName: String = "", message: String = "") {
            Crashlytics.log(Log.ERROR, logTag, "FirebaseService.kt::$functionName: Error in task - Message: $message Result: ${task.result.toString()}; Exception: ${task.exception.toString()}.")
        }

        fun logDebugTask(task: Task<*>, logTag: String = "", functionName: String = "", message: String = "") {
            Crashlytics.log(Log.DEBUG, logTag, "FirebaseService.kt::$functionName: Task result - Message: $message Result: ${task.result.toString()}; Exception(if any): ${task.exception.toString()}.")
        }
    }
}