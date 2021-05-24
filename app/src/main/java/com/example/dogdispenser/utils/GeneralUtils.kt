package com.example.dogdispenser.utils

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class GeneralUtils(
) {

    companion object {
        fun toast(context: Context, message: String, error: Boolean = true) {
            if (error)
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            else
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

        }

        fun hideKeyboard(context: Context) {
            val imm: InputMethodManager = context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }
    }


}