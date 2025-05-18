package io.github.zimoyin.asdk

import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity

fun getToast(context: Context): Toast = Toast.makeText(context, null, Toast.LENGTH_SHORT)

fun Toast.show(text: Any?) {
    setText(text?.toString() ?: "null")
    show()
}

