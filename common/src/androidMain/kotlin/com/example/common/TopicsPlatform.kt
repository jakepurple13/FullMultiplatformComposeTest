package com.example.common

import android.content.Context
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.FontRes
import androidx.annotation.IdRes
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.common.components.IconsButton
import com.example.common.screens.App
import com.example.common.screens.FavoritesUi
import com.example.common.screens.GithubTopicUI
import com.example.common.screens.TopicDrawer
import com.example.common.viewmodels.*
import io.noties.markwon.Markwon
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.coil.CoilImagesPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import io.noties.markwon.syntax.Prism4jThemeDarkula
import io.noties.markwon.syntax.SyntaxHighlightPlugin
import io.noties.prism4j.Prism4j
import io.noties.prism4j.annotations.PrismBundle
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
    val colorScheme = if (themeColors == ThemeColors.Default) when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDarkMode) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        else -> ThemeColors.Default.getThemeScheme(isDarkMode)
    } else themeColors.getThemeScheme(isDarkMode)
    MaterialTheme(colorScheme = colorScheme.animate(), content = content)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal actual fun SwipeRefreshWrapper(
    paddingValues: PaddingValues,
    isRefreshing: Boolean,
    onRefresh: suspend () -> Unit,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val pullRefreshState = rememberPullRefreshState(isRefreshing, onRefresh = { scope.launch { onRefresh() } })
    Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
        content()
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier
                .padding(paddingValues)
                .align(Alignment.TopCenter),
            backgroundColor = MaterialTheme.colorScheme.surface,
            contentColor = contentColorFor(MaterialTheme.colorScheme.surface),
            scale = true
        )
    }
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
    MarkdownText(markdown = text, modifier = modifier)
}

@Composable
internal fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    @FontRes fontResource: Int? = null,
    style: TextStyle = LocalTextStyle.current,
    @IdRes viewId: Int? = null,
    onClick: (() -> Unit)? = null,
    // this option will disable all clicks on links, inside the markdown text
    // it also enable the parent view to receive the click event
    disableLinkMovementMethod: Boolean = false,
    imageLoader: ImageLoader? = null,
) {
    val defaultColor: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
    val context: Context = LocalContext.current
    val markdownRender: Markwon = remember { createMarkdownRender(context, imageLoader) }
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            createTextView(
                context = ctx,
                color = color,
                defaultColor = defaultColor,
                fontSize = fontSize,
                fontResource = fontResource,
                maxLines = maxLines,
                style = style,
                textAlign = textAlign,
                viewId = viewId,
                onClick = onClick,
            )
        },
        update = { textView ->
            markdownRender.setMarkdown(textView, markdown)
            if (disableLinkMovementMethod) {
                textView.movementMethod = null
            }
        }
    )
}

@PrismBundle(includeAll = true)
public class PrismBuilder

private fun createTextView(
    context: Context,
    color: Color = Color.Unspecified,
    defaultColor: Color,
    fontSize: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    @FontRes fontResource: Int? = null,
    style: TextStyle,
    @IdRes viewId: Int? = null,
    onClick: (() -> Unit)? = null
): TextView {

    val textColor = color.takeOrElse { style.color.takeOrElse { defaultColor } }
    val mergedStyle = style.merge(
        TextStyle(
            color = textColor,
            fontSize = if (fontSize != TextUnit.Unspecified) fontSize else style.fontSize,
            textAlign = textAlign,
        )
    )
    return TextView(context).apply {
        onClick?.let { setOnClickListener { onClick() } }
        setTextColor(textColor.toArgb())
        setMaxLines(maxLines)
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, mergedStyle.fontSize.value)

        viewId?.let { id = viewId }
        textAlign?.let { align ->
            textAlignment = when (align) {
                TextAlign.Left, TextAlign.Start -> View.TEXT_ALIGNMENT_TEXT_START
                TextAlign.Right, TextAlign.End -> View.TEXT_ALIGNMENT_TEXT_END
                TextAlign.Center -> View.TEXT_ALIGNMENT_CENTER
                else -> View.TEXT_ALIGNMENT_TEXT_START
            }
        }

        fontResource?.let { font ->
            typeface = ResourcesCompat.getFont(context, font)
        }
    }
}

private fun createMarkdownRender(context: Context, imageLoader: ImageLoader?): Markwon {
    val coilImageLoader = imageLoader ?: ImageLoader.Builder(context)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .crossfade(true)
        .build()

    return Markwon.builder(context)
        .usePlugin(HtmlPlugin.create())
        .usePlugin(CoilImagesPlugin.create(context, coilImageLoader))
        .usePlugin(StrikethroughPlugin.create())
        .usePlugin(TablePlugin.create(context))
        .usePlugin(LinkifyPlugin.create())
        .usePlugin(SyntaxHighlightPlugin.create(Prism4j(GrammarLocatorDef()), Prism4jThemeDarkula.create()))
        .build()
}


public class TopicViewModel : ViewModel(), BaseTopicVM by BaseTopicViewModel() {

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

public class RepoViewModel(t: String) : ViewModel(), RepoVM by BaseRepoViewModel(t)

public class FavoritesViewModel : ViewModel(), FavoritesVM by BaseFavoritesViewModel() {
    override fun addFavorite(repo: GitHubTopic) {
        viewModelScope.launch { items.add(repo) }
    }

    override fun removeFavorite(repo: GitHubTopic) {
        viewModelScope.launch { items.remove(repo) }
    }
}

@Composable
public fun AppShow(vm: BaseTopicVM, favoritesVM: FavoritesVM) {
    App(vm, favoritesVM)
}

@Composable
public fun FavoritesUI(favoritesVM: FavoritesVM, backAction: () -> Unit) {
    FavoritesUi(favoritesVM, backAction)
}