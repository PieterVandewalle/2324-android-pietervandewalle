package com.pietervandewalle.androidapp.ui.articles

import com.pietervandewalle.androidapp.model.Article

data class ArticleOverviewState(
    val articles: List<Article>,
)

sealed interface ArticleApiState {
    data class Success(val articles: List<Article>) : ArticleApiState
    object Error : ArticleApiState
    object Loading : ArticleApiState
}
