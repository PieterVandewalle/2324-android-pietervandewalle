package com.pietervandewalle.androidapp.ui.articles

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pietervandewalle.androidapp.AndroidApplication
import com.pietervandewalle.androidapp.data.ArticleRepository
import com.pietervandewalle.androidapp.data.ArticleSampler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class ArticleOverviewViewModel(private val articleRepository: ArticleRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ArticleOverviewState(ArticleSampler.getAll()))
    val uiState: StateFlow<ArticleOverviewState> = _uiState.asStateFlow()

    private val useApi = true

    var articleApiState: ArticleApiState by mutableStateOf(ArticleApiState.Loading)
        private set

    var articleApiRefreshingState: ArticleApiState by mutableStateOf(
        ArticleApiState.Success(
            mutableListOf(),
        ),
    )
        private set

    init {
        if (useApi) {
            getApiArticles()
        } else {
            articleApiState = ArticleApiState.Success(ArticleSampler.getAll())
        }
    }

    private fun getApiArticles() {
        viewModelScope.launch {
            try {
                val listResult = articleRepository.getArticles()
                _uiState.update {
                    it.copy(articles = listResult)
                }
                articleApiState = ArticleApiState.Success(listResult)
            } catch (e: IOException) {
                articleApiState = ArticleApiState.Error
            }
        }
    }

    fun refresh() {
        // Don't refresh if still in initial load
        if (articleApiState is ArticleApiState.Loading) {
            return
        }

        articleApiRefreshingState = ArticleApiState.Loading
        viewModelScope.launch {
            try {
                val listResult = articleRepository.getArticles()
                _uiState.update {
                    it.copy(articles = listResult)
                }
                articleApiRefreshingState = ArticleApiState.Success(listResult)

                // if first load was error and refresh was successful, we want to display the items now
                if (articleApiState is ArticleApiState.Error) {
                    articleApiState = ArticleApiState.Success(listResult)
                }
            } catch (e: IOException) {
                articleApiRefreshingState = ArticleApiState.Error
            }
        }
    }

    companion object {
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
