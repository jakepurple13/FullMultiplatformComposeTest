package com.example.common.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.common.GitHubTopic

public class BaseFavoritesViewModel : FavoritesVM {
    override val items: SnapshotStateList<GitHubTopic> = mutableStateListOf()

    //override val db: FavoritesDatabase by lazy { FavoritesDatabase(database) }
    override fun addFavorite(repo: GitHubTopic): Unit = Unit
    override fun removeFavorite(repo: GitHubTopic): Unit = Unit
}

public interface FavoritesVM {
    public val items: SnapshotStateList<GitHubTopic>
    //val db: FavoritesDatabase

    public fun addFavorite(repo: GitHubTopic)
    public fun removeFavorite(repo: GitHubTopic)
}