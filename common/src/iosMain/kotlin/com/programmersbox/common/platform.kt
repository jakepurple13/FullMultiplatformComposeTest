package com.programmersbox.common

import androidx.compose.ui.window.Application
import platform.UIKit.UIViewController

actual fun getPlatformName(): String {
    return "iOS"
}

fun MainViewController(): UIViewController = Application("Falling Balls") { MainApp() }