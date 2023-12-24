package com.pietervandewalle.androidapp.fake

import com.pietervandewalle.androidapp.data.repo.ArticleRepository
import com.pietervandewalle.androidapp.model.Article
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeArticleRepository : ArticleRepository {
    override suspend fun insert(article: Article) {

    }

    override fun getAll(): Flow<List<Article>> = flow {
    }

    override fun getById(id: Int): Flow<Article> {
        TODO("Not yet implemented")
    }

    override suspend fun refresh() {
        TODO("Not yet implemented")
    }
}
