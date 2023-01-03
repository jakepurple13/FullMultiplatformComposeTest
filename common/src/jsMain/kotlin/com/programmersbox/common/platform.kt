package com.programmersbox.common

import androidx.compose.runtime.Composable

actual fun getPlatformName(): String {
    return "JavaScript"
}

@Composable
fun UIShow() {
    MainApp()
}