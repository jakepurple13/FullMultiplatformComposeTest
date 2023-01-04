package com.programmersbox.common

import androidx.compose.runtime.remember
import androidx.compose.ui.window.Application
import com.example.common.AppShow
import com.example.common.FavoritesViewModel
import com.example.common.TopicViewModel
import platform.UIKit.UIViewController

public actual fun getPlatformName(): String {
    return "iOS"
}

//public fun MainViewController(): UIViewController = Application("Falling Balls") { MainApp() }
public fun MainViewController(): UIViewController = Application("Falling Balls") {
    AppShow(remember { TopicViewModel() }, remember { FavoritesViewModel() })
}