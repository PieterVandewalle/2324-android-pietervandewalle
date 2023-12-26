package com.pietervandewalle.androidapp.ui.articles.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pietervandewalle.androidapp.AndroidApplication
import com.pietervandewalle.androidapp.WhileUiSubscribed
import com.pietervandewalle.androidapp.core.Result
import com.pietervandewalle.androidapp.core.asResult
import com.pietervandewalle.androidapp.data.repo.ArticleRepository
import com.pietervandewalle.androidapp.model.Article
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Based on https://github.com/jshvarts/UiStatePlayground/blob/master/app/src/main/java/com/example/uistateplayground/ui/HomeViewModel.kt

/**
 * ViewModel for managing the article overview screen.
 *
 * @param articleRepository The repository responsible for fetching article data.
 */
class ArticleOverviewViewModel(private val articleRepository: ArticleRepository) : ViewModel() {
    private val articles: Flow<Result<List<Article>>> = articleRepository.getAll().asResult()
    private val isRefreshing = MutableStateFlow(false)

    private val isError = MutableStateFlow(false)

    val uiState: StateFlow<ArticleOverviewState> = combine(
        articles,
        isRefreshing,
        isError,
    ) { articlesResult, refreshing, errorOccurred ->
        val articles: ArticlesUiState = when (articlesResult) {
            is Result.Success -> ArticlesUiState.Success(articlesResult.data)
            is Result.Loading -> ArticlesUiState.Loading
            is Result.Error -> ArticlesUiState.Error
        }

        ArticleOverviewState(
            articles,
            refreshing,
            errorOccurred,
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = ArticleOverviewState(
            ArticlesUiState.Loading,
            isRefreshing = false,
            isError = false,
        ),
    )

    private val exceptionHandler = CoroutineExceptionHandler { context, exception ->
        viewModelScope.launch {
            isError.emit(true)
        }
    }

    /**
     * Refreshes the list of articles.
     */
    fun refresh() {
        viewModelScope.launch(exceptionHandler) {
            with(articleRepository) {
                val refreshArticlesDeferred = async { refresh() }
                isRefreshing.emit(true)
                try {
                    awaitAll(refreshArticlesDeferred)
                } finally {
                    isRefreshing.emit(false)
                }
            }
        }
    }

    /**
     * Should be called after the error message is shown to reset the error state.
     */
    fun onErrorConsumed() {
        viewModelScope.launch {
            isError.emit(false)
        }
    }

    companion object {
        /**
         * Factory for creating instances of ArticleOverviewViewModel.
         */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AndroidApplication)
                val articleRepository = application.container.articleRepository
                ArticleOverviewViewModel(
                    articleRepository = articleRepository,
                )
            }
        }
    }
}
