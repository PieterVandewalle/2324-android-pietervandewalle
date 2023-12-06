package com.pietervandewalle.androidapp.ui.articles.detail

import android.util.Log
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
import com.pietervandewalle.androidapp.model.Article
import com.pietervandewalle.androidapp.ui.navigation.DestinationsArgs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException

class ArticleDetailViewModel(private val articleRepository: ArticleRepository, private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _uiState = MutableStateFlow(ArticleDetailState(ArticleSampler.getAll().first()))
    val uiState: StateFlow<ArticleDetailState> = _uiState.asStateFlow()

    private val articleId: Int = savedStateHandle[DestinationsArgs.ARTICLE_ID_ARG]!!

    lateinit var uiArticleState: StateFlow<Article?>
    var articlesApiState: ArticleApiState by mutableStateOf(ArticleApiState.Loading)
        private set

    init {
        getRepoArticle(articleId)
    }

    private fun getRepoArticle(articleId: Int) {
        Log.i("articleId", articleId.toString())
        try {
            viewModelScope.launch { articleRepository.refresh() }
            uiArticleState = articleRepository.getById(articleId).stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = null,
            )
            articlesApiState = ArticleApiState.Success
        } catch (e: IOException) {
            articlesApiState = ArticleApiState.Error
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
