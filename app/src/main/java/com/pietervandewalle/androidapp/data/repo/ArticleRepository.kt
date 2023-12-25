package com.pietervandewalle.androidapp.data.repo

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.pietervandewalle.androidapp.data.database.dao.ArticleDao
import com.pietervandewalle.androidapp.data.database.entity.asDbArticle
import com.pietervandewalle.androidapp.data.database.entity.asDomainArticle
import com.pietervandewalle.androidapp.data.database.entity.asDomainArticles
import com.pietervandewalle.androidapp.model.Article
import com.pietervandewalle.androidapp.network.GhentApiService
import com.pietervandewalle.androidapp.network.asDomainObjects
import com.pietervandewalle.androidapp.network.getArticlesAsFlow
import com.pietervandewalle.androidapp.workers.ArticlesRefreshWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import java.util.concurrent.TimeUnit

interface ArticleRepository {
    /**
     * Retrieves all articles from the repository as a Flow of lists.
     *
     * @return A Flow emitting a list of articles.
     */
    fun getAll(): Flow<List<Article>>

    /**
     * Retrieves an article by its unique identifier from the repository as a Flow.
     *
     * @param id The unique identifier of the article to retrieve.
     * @return A Flow emitting the requested article.
     */
    fun getById(id: Int): Flow<Article>

    /**
     * Inserts an article into the repository.
     *
     * @param article The article to insert.
     */
    suspend fun insert(article: Article)

    /**
     * Refreshes the repository, typically by fetching updated data from a remote source.
     */
    suspend fun refresh()
}

/**
 * Implementation of [ArticleRepository] that caches articles using a local database
 * (represented by [articleDao]) and fetches data from a remote source (represented by [ghentApiService]).
 *
 * @param articleDao The data access object for the local database.
 * @param ghentApiService The API service for fetching remote data.
 * @param context The Android application context.
 */
class CachingArticleRepository(private val articleDao: ArticleDao, private val ghentApiService: GhentApiService, context: Context) :
    ArticleRepository {

    override fun getAll(): Flow<List<Article>> {
        return articleDao.getAll().map { it.asDomainArticles() }.onStart { startWorkers() }.onEach {
            if (it.isEmpty()) {
                refresh()
            }
        }
    }

    override fun getById(id: Int): Flow<Article> {
        return articleDao.getById(id).map { it.asDomainArticle() }
    }

    override suspend fun insert(article: Article) {
        articleDao.insert(article.asDbArticle())
    }

    override suspend fun refresh() {
        ghentApiService.getArticlesAsFlow().collect {
            for (article in it.results.asDomainObjects()) {
                insert(article)
            }
        }
    }
    private val workManager = WorkManager.getInstance(context)
    private fun startWorkers() {
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).setRequiresBatteryNotLow(true).build()

        // Refresh articles every hour
        val refreshArticlesPeriodicallyRequest =
            PeriodicWorkRequestBuilder<ArticlesRefreshWorker>(
                15,
                TimeUnit.MINUTES,
            )
                .setConstraints(constraints)
                .build()

        workManager.enqueueUniquePeriodicWork(
            "refreshArticlesPeriodically",
            ExistingPeriodicWorkPolicy.KEEP,
            refreshArticlesPeriodicallyRequest,
        )
    }
}
