package com.pietervandewalle.androidapp.data.repo

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.pietervandewalle.androidapp.data.database.ArticleDao
import com.pietervandewalle.androidapp.data.database.asDbArticle
import com.pietervandewalle.androidapp.data.database.asDomainArticle
import com.pietervandewalle.androidapp.data.database.asDomainArticles
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
    suspend fun insert(article: Article)
    fun getAll(): Flow<List<Article>>
    fun getById(id: Int): Flow<Article>
    suspend fun refresh()
}

class CachingArticleRepository(private val articleDao: ArticleDao, private val ghentApiService: GhentApiService, context: Context) :
    ArticleRepository {
    override suspend fun insert(article: Article) {
        articleDao.insert(article.asDbArticle())
    }

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
