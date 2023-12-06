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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ArticleDetailViewModel(private val articleRepository: ArticleRepository, private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val articleId: Int = savedStateHandle[DestinationsArgs.ARTICLE_ID_ARG]!!
    private val article: Flow<Result<Article>> = articleRepository.getById(articleId).asResult()
    private val isError = MutableStateFlow(false)

    val uiState: StateFlow<ArticleDetailState> = combine(
        article,
        isError,
    ) { articleResult, errorOccurred ->
        val article: ArticleDetailUiState = when (articleResult) {
            is Result.Success -> ArticleDetailUiState.Success(articleResult.data)
            is Result.Loading -> ArticleDetailUiState.Loading
            is Result.Error -> ArticleDetailUiState.Error
        }

        ArticleDetailState(
            article,
            errorOccurred,
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = ArticleDetailState(
            ArticleDetailUiState.Loading,
            isError = false,
        ),
    )

    fun onErrorConsumed() {
        viewModelScope.launch {
            isError.emit(false)
        }
    }

    companion object {
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
