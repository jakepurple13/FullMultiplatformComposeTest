package com.programmersbox.common

import androidx.compose.runtime.Composable

public actual fun getPlatformName(): String {
    return "Desktop"
}

@Composable
public fun UIShow() {
    MainApp()
}