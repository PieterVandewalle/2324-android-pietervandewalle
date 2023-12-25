package com.pietervandewalle.androidapp.ui.articles.overview

import androidx.compose.runtime.Immutable
import com.pietervandewalle.androidapp.model.Article

data class ArticleOverviewState(
    val articles: ArticlesUiState,
    val isRefreshing: Boolean,
    val isError: Boolean,
)

@Immutable
sealed interface ArticlesUiState {
    data class Success(val articles: List<Article>) : ArticlesUiState
    object Error : ArticlesUiState
    object Loading : ArticlesUiState
}
