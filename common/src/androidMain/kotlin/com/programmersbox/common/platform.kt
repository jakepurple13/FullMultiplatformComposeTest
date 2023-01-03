package com.programmersbox.common

import androidx.compose.runtime.Composable

actual fun getPlatformName(): String {
    return "Android"
}

@Composable
fun UIShow() {
    MainApp()
}