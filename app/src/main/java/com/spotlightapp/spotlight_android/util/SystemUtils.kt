package com.spotlightapp.spotlight_android.util

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.spotlightapp.spotlight_android.R

object SystemUtils {

    fun showKeyboard(activity: Activity?) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    fun hideKeyboard(context: Context?, view: View) {
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun setSnackbarDefaultOptions(snackbar: Snackbar?) {
        val snackbarView: View? = snackbar?.view
        val snackbarMessage: TextView? = snackbarView?.findViewById(R.id.snackbar_text)
        snackbarMessage?.setTextColor(Color.WHITE)
    }
}