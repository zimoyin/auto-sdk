package com.github.zimoyin.autosdk.ui.utils

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

private var buttonCount = 0

@Composable
fun Button(
    text: String = "Button${if (buttonCount <= 0) "" else " $buttonCount"}",
    onClick: (() -> Unit) = {}
) {
    buttonCount++
    Button(onClick = { onClick() }) {
        Text(text)
    }
}