package com.example.common.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.common.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

internal class BaseRepoViewModel(
    topic: String
) : RepoVM {
    override val item by lazy { Json.decodeFromString<GitHubTopic>(topic) }
    override var repoContent by mutableStateOf<ReadMeResponse>(ReadMeResponse.Loading)
    override var error by mutableStateOf(false)
    override var showWebView by mutableStateOf(false)

    override suspend fun load() {
        val cached = Cached.cache[item.htmlUrl]
        if (cached != null) {
            println("Loading from cache")
            repoContent = ReadMeResponse.Success(cached.repoContent)
        } else {
            println("Loading from url")
            Network.getReadMe(item.fullName).fold(
                onSuccess = {
                    repoContent = it
                    if (it is ReadMeResponse.Success) Cached.cache[item.htmlUrl] = CachedTopic(item, it.content)
                },
                onFailure = {
                    it.printStackTrace()
                    error = true
                }
            )
        }
    }
}

public interface RepoVM {
    public val item: GitHubTopic
    public var repoContent: ReadMeResponse
    public var error: Boolean
    public var showWebView: Boolean
    public suspend fun load()
}