package com.pietervandewalle.androidapp.ui.articles.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pietervandewalle.androidapp.AndroidApplication
import com.pietervandewalle.androidapp.data.ArticleRepository
import com.pietervandewalle.androidapp.data.ArticleSampler
import com.pietervandewalle.androidapp.ui.navigation.DestinationsArgs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class ArticleDetailViewModel(private val articleRepository: ArticleRepository, private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _uiState = MutableStateFlow(ArticleDetailState(ArticleSampler.getAll().first()))
    val uiState: StateFlow<ArticleDetailState> = _uiState.asStateFlow()

    private val articleTitle: String? = savedStateHandle[DestinationsArgs.ARTICLE_TITLE_ARG]

    var articlesApiState: ArticleApiState by mutableStateOf(ArticleApiState.Loading)
        private set

    init {
        getApiArticle(articleTitle ?: "")
    }

    private fun getApiArticle(articleTitle: String) {
        viewModelScope.launch {
            try {
                val result = articleRepository.getArticleByTitle(articleTitle)
                _uiState.update {
                    it.copy(article = result)
                }
                articlesApiState = ArticleApiState.Success(result)
            } catch (e: IOException) {
                articlesApiState = ArticleApiState.Error
            }
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
