package com.programmersbox.common

import androidx.compose.ui.window.Application
import platform.UIKit.UIViewController

public actual fun getPlatformName(): String {
    return "iOS"
}

public fun MainViewController(): UIViewController = Application("Falling Balls") { MainApp() }