package com.example.common

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList

public abstract class BrowserTabHandler<T> {
    protected abstract val tabs: MutableList<Tabs.Tab<T>>
    protected abstract val pinnedTabs: MutableList<Tabs.PinnedTab<T>>
    protected abstract var endTabs: Tabs.EndTab<T>?
    public abstract var selected: Int

    public val tabbed: List<Tabs<T>> get() = pinnedTabs + tabs + listOfNotNull(endTabs)

    protected abstract fun refreshTab()

    public open fun selectedTab(): Tabs<T>? = tabbed.getOrNull(selected)

    public open fun newPinnedTab(tab: T) {
        pinnedTabs.add(Tabs.PinnedTab(tab))
    }

    public open fun newTab(tab: T) {
        tabs.add(Tabs.Tab(tab))
    }

    public open fun hasEndTab(endTab: Boolean) {
        endTabs = if (endTab) {
            Tabs.EndTab()
        } else {
            null
        }
    }

    public open fun closePinnedTab(tab: Tabs.PinnedTab<T>) {
        val index = pinnedTabs.indexOf(tab)
        when {
            index < selected -> selected--

            index == selected -> {
                if (pinnedTabs.size > selected) {
                    refreshTab()
                } else {
                    selected--
                }
            }

            index > selected -> Unit
            else -> selected = 0
        }
        pinnedTabs.remove(tab)
    }

    public open fun closeTab(tab: Tabs.Tab<T>) {
        val index = tabbed.indexOf(tab)
        when {
            index < selected -> selected--

            index == selected -> {
                if (tabs.size + pinnedTabs.size - 1 > selected) {
                    refreshTab()
                } else {
                    selected--
                }
            }

            index > selected -> Unit
            else -> selected = 0
        }
        tabs.remove(tab)
    }

    public open fun nextTab() {
        if (selected == tabs.size + pinnedTabs.size - 1) {
            selected = 0
        } else {
            selected++
        }
    }

    public open fun previousTab() {
        if (selected == 0) {
            selected = tabs.size + pinnedTabs.size - 1
        } else {
            selected--
        }
    }

    public open fun selectTab(index: Int) {
        selected = index
    }

    public open fun pinTab(tab: Tabs.Tab<T>) {
        tabs.remove(tab)
        pinnedTabs.add(tab.pin())
    }

    public open fun unpin(tab: Tabs.PinnedTab<T>) {
        pinnedTabs.remove(tab)
        tabs.add(tab.unpin())
    }
}

public class BrowserTab<T>(initialTab: Int) : BrowserTabHandler<T>() {
    override val tabs: SnapshotStateList<Tabs.Tab<T>> = mutableStateListOf<Tabs.Tab<T>>()
    override val pinnedTabs: SnapshotStateList<Tabs.PinnedTab<T>> = mutableStateListOf<Tabs.PinnedTab<T>>()
    override var endTabs: Tabs.EndTab<T>? by mutableStateOf<Tabs.EndTab<T>?>(null)

    public val tabsList: List<IndexedValue<Tabs<T>>> by derivedStateOf {
        (pinnedTabs + tabs + listOfNotNull(endTabs))
            .withIndex()
            .toList()
    }

    public var refreshKey: Int by mutableStateOf(0)
    public override var selected: Int by mutableStateOf(initialTab)

    override fun refreshTab() {
        refreshKey++
    }
}

public sealed class Tabs<T> {
    public data class PinnedTab<T>(val data: T) : Tabs<T>() {
        public fun unpin(): Tab<T> = Tab(data)
    }

    public data class Tab<T>(val data: T) : Tabs<T>() {
        public fun pin(): PinnedTab<T> = PinnedTab(data)
    }

    public class EndTab<T> : Tabs<T>()
}
