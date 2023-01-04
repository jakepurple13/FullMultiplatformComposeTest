package com.example.common.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.common.GitHubTopic
import com.example.common.Network
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext

internal class BaseTopicViewModel : BaseTopicVM {

    override val items = mutableStateListOf<GitHubTopic>()
    override var isLoading by mutableStateOf(true)
    override var singleTopic by mutableStateOf(true)
    override val currentTopics = mutableStateListOf<String>()
    override val topicList = mutableStateListOf<String>()
    override var page by mutableStateOf(1)

    //override val db: Database by lazy { Database() }

    private suspend fun loadTopics() {
        isLoading = true
        withContext(SupervisorJob()) {
            Network.getTopics(page, *currentTopics.toTypedArray()).fold(
                onSuccess = { items.addAll(it) },
                onFailure = { it.printStackTrace() }
            )
        }
        isLoading = false
    }

    override suspend fun refresh() {
        items.clear()
        page = 1
        loadTopics()
    }

    override suspend fun newPage() {
        page++
        loadTopics()
    }

}

public interface BaseTopicVM {
    public fun setTopic(topic: String): Unit = Unit
    public fun addTopic(topic: String): Unit = Unit
    public fun removeTopic(topic: String): Unit = Unit
    public suspend fun refresh(): Unit = Unit
    public suspend fun newPage(): Unit = Unit
    public suspend fun toggleSingleTopic(): Unit = Unit
    //val db: Database

    public val items: SnapshotStateList<GitHubTopic>
    public var isLoading: Boolean
    public val currentTopics: SnapshotStateList<String>
    public val topicList: SnapshotStateList<String>
    public val page: Int
    public var singleTopic: Boolean
}