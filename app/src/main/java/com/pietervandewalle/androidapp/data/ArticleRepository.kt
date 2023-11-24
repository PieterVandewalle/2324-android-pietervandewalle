package com.pietervandewalle.androidapp.data

import com.pietervandewalle.androidapp.model.Article
import com.pietervandewalle.androidapp.network.GhentApiService
import com.pietervandewalle.androidapp.network.asDomainObjects

interface ArticleRepository {
    suspend fun getArticles(): List<Article>
}
class ApiArticleRepository(private val ghentApiService: GhentApiService) : ArticleRepository {
    override suspend fun getArticles(): List<Article> {
        return ghentApiService.getArticles().results.asDomainObjects()
    }
}
