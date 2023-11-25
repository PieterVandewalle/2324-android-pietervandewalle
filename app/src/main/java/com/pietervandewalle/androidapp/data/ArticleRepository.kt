package com.pietervandewalle.androidapp.data

import com.pietervandewalle.androidapp.model.Article
import com.pietervandewalle.androidapp.network.GhentApiService
import com.pietervandewalle.androidapp.network.asDomainObjects
import java.io.IOException

interface ArticleRepository {
    suspend fun getArticles(): List<Article>
    suspend fun getArticleByTitle(title: String): Article
}
class ApiArticleRepository(private val ghentApiService: GhentApiService) : ArticleRepository {
    override suspend fun getArticles(): List<Article> {
        return ghentApiService.getArticles().results.asDomainObjects()
    }

    override suspend fun getArticleByTitle(title: String): Article {
        return ghentApiService.getArticles().results.asDomainObjects().firstOrNull { article -> article.title == title } ?: throw IOException()
    }
}
