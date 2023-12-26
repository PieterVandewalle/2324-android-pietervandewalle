package com.pietervandewalle.androidapp.ui.articles.overview

import androidx.compose.runtime.Immutable
import com.pietervandewalle.androidapp.model.Article

/**
 * Represents the state of the article overview screen.
 *
 * @param articles The state of articles to display.
 * @param isRefreshing Indicates whether a refresh operation is in progress.
 * @param isError Indicates whether an error has occurred.
 */
data class ArticleOverviewState(
    val articles: ArticlesUiState,
    val isRefreshing: Boolean,
    val isError: Boolean,
)

/**
 * Represents the state of articles in the overview screen.
 */
@Immutable
sealed interface ArticlesUiState {
    /**
     * Represents a successful state with a list of articles.
     *
     * @param articles The list of articles to display.
     */
    data class Success(val articles: List<Article>) : ArticlesUiState

    /**
     * Represents an error state where an error occurred while fetching articles.
     */
    object Error : ArticlesUiState

    /**
     * Represents a loading state while fetching articles.
     */
    object Loading : ArticlesUiState
}
