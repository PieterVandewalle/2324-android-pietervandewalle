package com.pietervandewalle.androidapp.ui.articles.detail

import com.pietervandewalle.androidapp.model.Article

data class ArticleDetailState(
    val article: Article,
)

sealed interface ArticleApiState {
    data class Success(val article: Article) : ArticleApiState
    object Error : ArticleApiState
    object Loading : ArticleApiState
}
