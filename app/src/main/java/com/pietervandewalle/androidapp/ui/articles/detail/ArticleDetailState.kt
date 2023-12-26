package com.pietervandewalle.androidapp.ui.articles.detail

import androidx.compose.runtime.Immutable
import com.pietervandewalle.androidapp.model.Article

/**
 * Represents the state of an article detail screen.
 *
 * @property article The article detail UI state.
 */
data class ArticleDetailState(
    val article: ArticleDetailUiState,
)

/**
 * Represents the UI state of an article detail screen.
 */
@Immutable
sealed interface ArticleDetailUiState {

    /**
     * Represents a successful state with the [article] data.
     *
     * @property article The article data.
     */
    data class Success(val article: Article) : ArticleDetailUiState

    /**
     * Represents an error state.
     */
    object Error : ArticleDetailUiState

    /**
     * Represents a loading state.
     */
    object Loading : ArticleDetailUiState
}
