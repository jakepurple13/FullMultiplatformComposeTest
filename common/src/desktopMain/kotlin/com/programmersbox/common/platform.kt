package com.programmersbox.common

import androidx.compose.runtime.Composable

actual fun getPlatformName(): String {
    return "Desktop"
}

@Composable
fun UIShow() {
    MainApp()
}