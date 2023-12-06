package com.pietervandewalle.androidapp.ui.articles.overview

import androidx.compose.runtime.Immutable
import com.pietervandewalle.androidapp.model.Article

data class ArticleOverviewState(
    val articles: ArticlesOverviewUiState,
    val isRefreshing: Boolean,
    val isError: Boolean,
)

@Immutable
sealed interface ArticlesOverviewUiState {
    data class Success(val articles: List<Article>) : ArticlesOverviewUiState
    object Error : ArticlesOverviewUiState
    object Loading : ArticlesOverviewUiState
}
