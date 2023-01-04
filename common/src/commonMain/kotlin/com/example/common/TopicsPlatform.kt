package com.example.common

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.common.viewmodels.BaseTopicVM
import com.example.common.viewmodels.FavoritesVM
import com.example.common.viewmodels.RepoVM

internal expect val refreshIcon: Boolean

internal expect val useInfiniteLoader: Boolean

@Composable
internal expect fun TopicItemModification(item: GitHubTopic, content: @Composable () -> Unit)

@Composable
internal expect fun TopicDrawerLocation(vm: BaseTopicVM, favoritesVM: FavoritesVM)

@Composable
internal expect fun BoxScope.ReposScrollBar(lazyListState: LazyListState)

@Composable
internal expect fun BoxScope.ScrollBar(scrollState: ScrollState)

@Composable
internal expect fun BoxScope.LoadingIndicator(vm: BaseTopicVM)

@Composable
internal expect fun SwipeRefreshWrapper(
    paddingValues: PaddingValues,
    isRefreshing: Boolean,
    onRefresh: suspend () -> Unit,
    content: @Composable () -> Unit
)

@Composable
internal expect fun M3MaterialThemeSetup(themeColors: ThemeColors, isDarkMode: Boolean, content: @Composable () -> Unit)

@Composable
internal expect fun MarkdownText(text: String, modifier: Modifier)

@Composable
internal expect fun LibraryContainer(modifier: Modifier)

@Composable
internal expect fun RowScope.RepoViewToggle(repoVM: RepoVM)

@Composable
internal expect fun RepoContentView(repoVM: RepoVM, modifier: Modifier, defaultContent: @Composable () -> Unit)

@Composable
internal expect fun ImageLoader(url: () -> String, modifier: Modifier)