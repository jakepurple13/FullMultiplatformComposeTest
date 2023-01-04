package com.example.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.example.common.screens.App
import com.example.common.screens.FavoritesUi
import com.example.common.screens.GithubTopicUI
import com.example.common.screens.TopicDrawer
import com.example.common.viewmodels.*
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.cef.browser.CefBrowser
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import java.awt.Cursor


public actual val refreshIcon: Boolean = true

public actual val useInfiniteLoader: Boolean = false

@Composable
public actual fun TopicItemModification(item: GitHubTopic, content: @Composable () -> Unit) {
    val actions = LocalAppActions.current
    val uriHandler = LocalUriHandler.current
    ContextMenuArea(
        items = {
            listOf(
                ContextMenuItem("Open") { actions.onCardClick(item) },
                ContextMenuItem("Open in New Tab") { actions.onNewTabOpen(item) },
                ContextMenuItem("Open in New Window") { actions.onNewWindow(item) },
                ContextMenuItem("Open in Browser") { uriHandler.openUri(item.htmlUrl) },
                ContextMenuItem("Share") { actions.onShareClick(item) },
            )
        },
        content = content
    )
}

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
public actual fun TopicDrawerLocation(vm: BaseTopicVM, favoritesVM: FavoritesVM) {
    val splitter = rememberSplitPaneState()

    HorizontalSplitPane(splitPaneState = splitter) {
        first(250.dp) { TopicDrawer(vm) }
        second(550.dp) { GithubTopicUI(vm, favoritesVM) }

        splitter {
            visiblePart {
                Box(
                    Modifier.width(2.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.onBackground)
                )
            }
            handle {
                Box(
                    Modifier
                        .markAsHandle()
                        .pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))
                        .background(SolidColor(Color.Gray), alpha = 0.50f)
                        .width(2.dp)
                        .fillMaxHeight()
                )
            }
        }
    }
}

@Composable
public actual fun LibraryContainer(modifier: Modifier) {
    /*val uriHandler = LocalUriHandler.current
    LibrariesContainer(
        useResource("aboutlibraries.json") { it.bufferedReader().readText() },
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
public actual fun M3MaterialThemeSetup(themeColors: ThemeColors, isDarkMode: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = themeColors.getThemeScheme(isDarkMode).animate(), content = content)
}

@Composable
public actual fun BoxScope.ReposScrollBar(lazyListState: LazyListState) {
    VerticalScrollbar(
        adapter = rememberScrollbarAdapter(lazyListState),
        modifier = Modifier
            .align(Alignment.CenterEnd)
            .fillMaxHeight()
            .padding(end = 4.dp)
    )
}

@Composable
public actual fun BoxScope.ScrollBar(scrollState: ScrollState) {
    VerticalScrollbar(
        adapter = rememberScrollbarAdapter(scrollState),
        modifier = Modifier
            .align(Alignment.CenterEnd)
            .fillMaxHeight()
    )
}

@Composable
public actual fun SwipeRefreshWrapper(
    paddingValues: PaddingValues,
    isRefreshing: Boolean,
    onRefresh: suspend () -> Unit,
    content: @Composable () -> Unit
): Unit = content()

@Composable
public actual fun BoxScope.LoadingIndicator(vm: BaseTopicVM) {
    AnimatedVisibility(vm.isLoading, modifier = Modifier.align(Alignment.TopCenter)) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.TopCenter))
    }
}

@Composable
public actual fun MarkdownText(text: String, modifier: Modifier) {
    /*Markdown(
        content = text,
        modifier = modifier
    )*/
}

@Composable
public actual fun RowScope.RepoViewToggle(repoVM: RepoVM) {
    NavigationBarItem(
        selected = repoVM.showWebView,
        onClick = { repoVM.showWebView = !repoVM.showWebView },
        icon = {
            Icon(
                if (repoVM.showWebView) Icons.Default.WebAsset else Icons.Default.WebAssetOff,
                null
            )
        },
        label = { Text("Show WebView") }
    )

    AnimatedVisibility(
        repoVM.showWebView,
        modifier = Modifier.weight(1f)
    ) {
        val browser = (repoVM as RepoViewModel).browser
        Row {
            NavigationBarItem(
                selected = false,
                onClick = { browser.goBack() },
                icon = { Icon(Icons.Default.ArrowCircleLeft, null) },
                label = { Text("Back") },
                enabled = repoVM.showWebView
            )

            NavigationBarItem(
                selected = false,
                onClick = { browser.goForward() },
                icon = { Icon(Icons.Default.ArrowCircleRight, null) },
                label = { Text("Forward") },
                enabled = repoVM.showWebView
            )
        }
    }
}

@Composable
public actual fun RepoContentView(repoVM: RepoVM, modifier: Modifier, defaultContent: @Composable () -> Unit) {
    Crossfade(repoVM.showWebView) { target ->
        when (target) {
            true -> WebView((repoVM as RepoViewModel).browser.uiComponent, modifier)
            false -> defaultContent()
        }
    }
}

@Composable
public actual fun ImageLoader(url: () -> String, modifier: Modifier) {
    KamelImage(
        lazyPainterResource(url()),
        modifier = Modifier.size(48.dp),
        contentDescription = null,
        animationSpec = tween()
    )
}

public class TopicViewModel(private val viewModelScope: CoroutineScope) :
    BaseTopicVM by BaseTopicViewModel() {

    /*init {
        s
            .map { it.topicList }
            .distinctUntilChanged()
            .onEach {
                topicList.clear()
                topicList.addAll(it)
            }
            .launchIn(viewModelScope)

        s
            .map { it.currentTopics }
            .distinctUntilChanged()
            .onEach {
                currentTopics.clear()
                currentTopics.addAll(it)
                if (it.isNotEmpty()) refresh()
            }
            .launchIn(viewModelScope)

        s
            .map { it.singleTopic }
            .distinctUntilChanged()
            .onEach { singleTopic = it }
            .launchIn(viewModelScope)
    }*/

    override fun setTopic(topic: String) {
        viewModelScope.launch {
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
            if (currentTopics.isNotEmpty()) refresh()
        }
    }

    override fun addTopic(topic: String) {
        viewModelScope.launch {
            if (topic !in topicList) {
                topicList.add(topic)
            }
        }
    }

    override fun removeTopic(topic: String) {
        viewModelScope.launch {
            topicList.remove(topic)
        }
    }

    override suspend fun toggleSingleTopic() {
        singleTopic = !singleTopic
    }
}

public class RepoViewModel(t: String, browserHandler: BrowserHandler) : RepoVM by BaseRepoViewModel(t) {
    public val browser: CefBrowser by lazy { browserHandler.createBrowser(item.htmlUrl) }
}

public class FavoritesViewModel(
    private val viewModelScope: CoroutineScope
) : FavoritesVM by BaseFavoritesViewModel() {
    /*init {
        viewModelScope.launch {
            db.favoriteRepos()
                .distinctUntilChanged()
                .onEach {
                    items.clear()
                    items.addAll(it)
                }
                .collect()
        }
    }

    override fun addFavorite(repo: GitHubTopic) {
        viewModelScope.launch { db.addFavorite(repo) }
    }

    override fun removeFavorite(repo: GitHubTopic) {
        viewModelScope.launch { db.removeFavorite(repo) }
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