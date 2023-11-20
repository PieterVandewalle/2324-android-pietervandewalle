package com.pietervandewalle.androidapp.ui.articles

import androidx.lifecycle.ViewModel
import com.pietervandewalle.androidapp.data.ArticleSampler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ArticleOverviewViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ArticleOverviewState(ArticleSampler.getAll()))
    val uiState: StateFlow<ArticleOverviewState> = _uiState.asStateFlow()
}
