package com.pietervandewalle.androidapp.fake.dao

import com.pietervandewalle.androidapp.data.database.dao.ArticleDao
import com.pietervandewalle.androidapp.data.database.entity.DbArticle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeArticleDao(initialArticles: List<DbArticle>? = emptyList()) : ArticleDao {
    private var _articles: MutableMap<Int, DbArticle>? = null

    var articles: List<DbArticle>?
        get() = _articles?.values?.toList()?.sortedWith(
            compareByDescending<DbArticle> { it.date }
                .thenBy { it.title },
        )
        set(newQuotations) {
            _articles = newQuotations?.associateBy { it.id }?.toMutableMap()
        }

    init {
        articles = initialArticles
    }
    override suspend fun insert(article: DbArticle) {
        val uniqueId = (articles?.size ?: 0) + 1
        var articleToInsert = article
        if (article.id == 0) {
            articleToInsert = article.copy(id = uniqueId)
        }
        articles = articles?.plus(articleToInsert)
    }

    override fun getAll(): Flow<List<DbArticle>> = flow {
        emit(articles ?: emptyList())
    }

    override fun getById(id: Int): Flow<DbArticle> = flow {
        emit(articles?.firstOrNull { it.id == id } ?: throw Exception("Article not found"))
    }
}
