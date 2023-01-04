package com.example.common

import androidx.compose.runtime.Immutable

internal object AppInfo {
    const val VERSION = "1.0"
}

@Immutable
public data class AppActions(
    val onCardClick: (GitHubTopic) -> Unit = {},
    val onNewTabOpen: (GitHubTopic) -> Unit = {},
    val onNewWindow: (GitHubTopic) -> Unit = {},
    val onShareClick: (GitHubTopic) -> Unit = {},
    val onSettingsClick: () -> Unit = {},
    val showLibrariesUsed: () -> Unit = {},
    val showFavorites: () -> Unit = {}
)