package com.pietervandewalle.androidapp.ui.articles.detail

import androidx.compose.runtime.Immutable
import com.pietervandewalle.androidapp.model.Article

data class ArticleDetailState(
    val article: ArticleDetailUiState,
    val isError: Boolean,
)

@Immutable
sealed interface ArticleDetailUiState {
    data class Success(val article: Article) : ArticleDetailUiState
    object Error : ArticleDetailUiState
    object Loading : ArticleDetailUiState
}
