package com.pietervandewalle.androidapp.ui.articles.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pietervandewalle.androidapp.AndroidApplication
import com.pietervandewalle.androidapp.WhileUiSubscribed
import com.pietervandewalle.androidapp.core.Result
import com.pietervandewalle.androidapp.core.asResult
import com.pietervandewalle.androidapp.data.repo.ArticleRepository
import com.pietervandewalle.androidapp.model.Article
import com.pietervandewalle.androidapp.ui.navigation.DestinationsArgs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for managing the details of an article.
 *
 * @param articleRepository The repository responsible for fetching article data.
 * @param savedStateHandle The SavedStateHandle for retrieving and saving the article ID.
 */
class ArticleDetailViewModel(private val articleRepository: ArticleRepository, private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val articleId: Int = savedStateHandle[DestinationsArgs.ARTICLE_ID_ARG]!!
    private val article: Flow<Result<Article>> = articleRepository.getById(articleId).asResult()

    val uiState: StateFlow<ArticleDetailState> = article.map { articleResult ->
        val article: ArticleDetailUiState = when (articleResult) {
            is Result.Success -> ArticleDetailUiState.Success(articleResult.data)
            is Result.Loading -> ArticleDetailUiState.Loading
            is Result.Error -> ArticleDetailUiState.Error
        }

        ArticleDetailState(
            article,
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = ArticleDetailState(
            ArticleDetailUiState.Loading,
        ),
    )

    companion object {
        /**
         * Factory for creating instances of ArticleDetailViewModel.
         */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AndroidApplication)
                val articleRepository = application.container.articleRepository

                ArticleDetailViewModel(
                    articleRepository = articleRepository,
                    savedStateHandle = createSavedStateHandle(),
                )
            }
        }
    }
}
