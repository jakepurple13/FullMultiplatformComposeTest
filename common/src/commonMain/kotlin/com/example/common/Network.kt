package com.example.common

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
internal data class GithubTopics(
    val items: List<GitHubTopic>
)

@Serializable
public data class GitHubTopic(
    @SerialName("html_url")
    val htmlUrl: String,
    val url: String,
    val name: String,
    @SerialName("full_name")
    val fullName: String,
    val description: String? = null,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("pushed_at")
    val pushedAt: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("stargazers_count")
    val stars: Int,
    val watchers: Int,
    @SerialName("forks_count")
    val forks: Int = 0,
    val language: String = "No language",
    val owner: Owner,
    val license: License? = null,
    @SerialName("default_branch")
    val branch: String,
    val topics: List<String> = emptyList()
)

@Serializable
public data class Owner(
    @SerialName("avatar_url")
    val avatarUrl: String? = null
)

@Serializable
public data class License(
    val name: String,
)

@Serializable
internal data class GitHubRepo(
    val content: String,
    val encoding: String,
)

@Serializable
public sealed class ReadMeResponse {
    public class Success(public val content: String) : ReadMeResponse()

    @Serializable
    public class Failed(public val message: String) : ReadMeResponse()

    public object Loading : ReadMeResponse()
}

internal object Network {
    private val json = Json {
        isLenient = true
        prettyPrint = true
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val client by lazy {
        HttpClient {
            install(Logging) {
                //logger = Logger.SIMPLE
                //level = LogLevel.ALL
            }
            install(ContentNegotiation) { json(json) }
        }
    }

    //private val timePrinter = PrettyTime()
    //private val format = SimpleDateFormat.getDateTimeInstance()

    suspend fun getTopics(page: Int, vararg topics: String) = runCatching {
        client.get("https://api.github.com/search/repositories") {
            url {
                parameters.append("page", page.toString())
                parameters.append("sort", "updated")
                parameters.append("order", "desc")
                parameters.append("q", topics.joinToString(separator = "+") { "topic:$it" })
            }
        }.body<GithubTopics>().items/*.items.map {
            val date = Instant.parse(it.pushedAt).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            it.copy(pushedAt = "Updated " + timePrinter.format(Date(date)) + " on\n" + format.format(date))
        }*/
    }

    suspend fun getReadMe(fullName: String) = runCatching {
        val response = client.get("https://api.github.com/repos/$fullName/readme") {
            header("Accept", "application/vnd.github.raw+json")
        }.bodyAsText()

        try {
            json.decodeFromString<ReadMeResponse.Failed>(response)
        } catch (e: Exception) {
            ReadMeResponse.Success(response)
        }
    }
}