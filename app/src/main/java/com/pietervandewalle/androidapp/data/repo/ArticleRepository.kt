package com.pietervandewalle.androidapp.data.repo

import android.util.Log
import com.pietervandewalle.androidapp.data.database.ArticleDao
import com.pietervandewalle.androidapp.data.database.asDbArticle
import com.pietervandewalle.androidapp.data.database.asDomainArticle
import com.pietervandewalle.androidapp.data.database.asDomainArticles
import com.pietervandewalle.androidapp.model.Article
import com.pietervandewalle.androidapp.network.GhentApiService
import com.pietervandewalle.androidapp.network.asDomainObjects
import com.pietervandewalle.androidapp.network.getArticlesAsFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

interface ArticleRepository {
    suspend fun insert(article: Article)
    fun getAll(): Flow<List<Article>>
    fun getById(id: Int): Flow<Article>
    suspend fun refresh()
}

class CachingArticleRepository(private val articleDao: ArticleDao, private val ghentApiService: GhentApiService) :
    ArticleRepository {
    override suspend fun insert(article: Article) {
        articleDao.insert(article.asDbArticle())
    }

    override fun getAll(): Flow<List<Article>> {
        return articleDao.getAll().map { it.asDomainArticles() }.onEach {
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
                Log.i("TEST", "refresh: $article")
                insert(article)
            }
        }
    }
}
