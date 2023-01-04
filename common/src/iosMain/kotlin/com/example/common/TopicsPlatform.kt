package com.example.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.platform.LocalUriHandler
import com.example.common.components.IconsButton
import com.example.common.screens.App
import com.example.common.screens.FavoritesUi
import com.example.common.screens.GithubTopicUI
import com.example.common.screens.TopicDrawer
import com.example.common.viewmodels.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


internal actual val refreshIcon = false

internal actual val useInfiniteLoader = true

@Composable
internal actual fun TopicItemModification(item: GitHubTopic, content: @Composable () -> Unit) {
    content()
}

@OptIn(ExperimentalMaterial3Api::class)
public val LocalTopicDrawerState: ProvidableCompositionLocal<DrawerState> =
    staticCompositionLocalOf<DrawerState> { error("Nothing Here!") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal actual fun TopicDrawerLocation(
    vm: BaseTopicVM,
    favoritesVM: FavoritesVM
) {
    val drawerState = LocalTopicDrawerState.current
    val scope = rememberCoroutineScope()

    DismissibleNavigationDrawer(
        drawerContent = { TopicDrawer(vm) },
        drawerState = drawerState
    ) {
        GithubTopicUI(
            vm = vm,
            favoritesVM = favoritesVM,
            navigationIcon = {
                IconsButton(
                    onClick = { scope.launch { if (drawerState.isOpen) drawerState.close() else drawerState.open() } },
                    icon = Icons.Default.Menu
                )
            }
        )
    }
}

@Composable
internal actual fun LibraryContainer(modifier: Modifier) {
    val uriHandler = LocalUriHandler.current
    /*LibrariesContainer(
        modifier = modifier,
        onLibraryClick = { it.website?.let(uriHandler::openUri) },
        colors = LibraryDefaults.libraryColors(
            backgroundColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
            badgeBackgroundColor = MaterialTheme.colorScheme.primaryContainer,
            badgeContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )*/
}

@Composable
internal actual fun BoxScope.ReposScrollBar(lazyListState: LazyListState) {
}

@Composable
internal actual fun BoxScope.ScrollBar(scrollState: ScrollState) {
}

@Composable
internal actual fun M3MaterialThemeSetup(
    themeColors: ThemeColors,
    isDarkMode: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(colorScheme = themeColors.getThemeScheme(isDarkMode).animate(), content = content)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal actual fun SwipeRefreshWrapper(
    paddingValues: PaddingValues,
    isRefreshing: Boolean,
    onRefresh: suspend () -> Unit,
    content: @Composable () -> Unit
) {
    content()
}

@Composable
internal actual fun BoxScope.LoadingIndicator(vm: BaseTopicVM) {
}

@Composable
internal actual fun RowScope.RepoViewToggle(repoVM: RepoVM) {
}

@Composable
internal actual fun RepoContentView(repoVM: RepoVM, modifier: Modifier, defaultContent: @Composable () -> Unit) {
    defaultContent()
}

@Composable
internal actual fun MarkdownText(text: String, modifier: Modifier) {

}

@Composable
public actual fun ImageLoader(url: () -> String, modifier: Modifier) {
    val imageInfo by produceState<ImageBitmap?>(null) {
        value = org.jetbrains.skia.Image.makeFromEncoded(HttpClient().get(url()).readBytes()).toComposeImageBitmap()
    }
    imageInfo?.let { Image(it, null, modifier) }
}

public class TopicViewModel : BaseTopicVM by BaseTopicViewModel() {

    private val job = CoroutineScope(SupervisorJob())

    override fun setTopic(topic: String) {
        job.launch {
            if (singleTopic) {
                currentTopics.clear()
                currentTopics.add(topic)
            } else {
                if (topic !in currentTopics) {
                    currentTopics.add(topic)
                } else {
                    currentTopics.remove(topic)
                }
            }
        }
    }

    override fun addTopic(topic: String) {
        job.launch {
            if (topic !in topicList) {
                topicList.add(topic)
            }
        }
    }

    override fun removeTopic(topic: String) {
        job.launch {
            topicList.remove(topic)
        }
    }

    override suspend fun toggleSingleTopic() {
        singleTopic = !singleTopic
    }
}

public class RepoViewModel(t: String) : RepoVM by BaseRepoViewModel(t)

public class FavoritesViewModel : FavoritesVM by BaseFavoritesViewModel() {
    /*override fun addFavorite(repo: GitHubTopic) {
        viewModelScope.launch { items.add(repo) }
    }

    override fun removeFavorite(repo: GitHubTopic) {
        viewModelScope.launch { items.remove(repo) }
    }*/
}

@Composable
public fun AppShow(vm: BaseTopicVM, favoritesVM: FavoritesVM) {
    App(vm, favoritesVM)
}

@Composable
public fun FavoritesUI(favoritesVM: FavoritesVM, backAction: () -> Unit) {
    FavoritesUi(favoritesVM, backAction)
}