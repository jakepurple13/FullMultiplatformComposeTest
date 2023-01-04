package com.programmersbox.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Application
import platform.UIKit.UIViewController

public actual fun getPlatformName(): String {
    return "iOS"
}

public fun MainViewController(): UIViewController = Application("Falling Balls") {
    Column {
        Spacer(Modifier.height(30.dp))
        MainApp()
    }
}