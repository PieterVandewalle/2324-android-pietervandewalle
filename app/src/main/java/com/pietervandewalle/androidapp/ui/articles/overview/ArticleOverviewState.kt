package com.pietervandewalle.androidapp.ui.articles.overview

import com.pietervandewalle.androidapp.model.Article

data class ArticleOverviewState(
    val articles: List<Article>,
    val articleInDetailView: Article? = null,
)

sealed interface ArticlesApiState {
    data class Success(val articles: List<Article>) : ArticlesApiState
    object Error : ArticlesApiState
    object Loading : ArticlesApiState
}
