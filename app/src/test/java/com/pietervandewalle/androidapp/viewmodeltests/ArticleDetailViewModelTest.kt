package com.pietervandewalle.androidapp.viewmodeltests

import androidx.lifecycle.SavedStateHandle
import com.pietervandewalle.androidapp.TestDispatcherRule
import com.pietervandewalle.androidapp.data.repo.CachingArticleRepository
import com.pietervandewalle.androidapp.data.sampler.ArticleSampler
import com.pietervandewalle.androidapp.ui.articles.detail.ArticleDetailUiState
import com.pietervandewalle.androidapp.ui.articles.detail.ArticleDetailViewModel
import com.pietervandewalle.androidapp.ui.navigation.DestinationsArgs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ArticleDetailViewModelTest {
    @get:Rule
    val testDispatcherRule = TestDispatcherRule()

    private lateinit var viewModel: ArticleDetailViewModel

    private val repoMock = mock<CachingArticleRepository>()

    private val sampleArticle = ArticleSampler.getAll().first()

    @Test
    fun `uiState reflects success and contains article on successful load`() = runTest {
        whenever(repoMock.getById(sampleArticle.id)).thenReturn(
            flow {
                delay(1000) // Simulated network delay
                emit(sampleArticle)
            },
        )

        viewModel = ArticleDetailViewModel(
            articleRepository = repoMock,
            savedStateHandle = SavedStateHandle(
                initialState = mapOf(
                    DestinationsArgs.ARTICLE_ID_ARG to sampleArticle.id,
                ),
            ),
        )

        // Create an empty collector for the StateFlow
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        Assert.assertTrue(viewModel.uiState.value.article is ArticleDetailUiState.Loading)

        advanceTimeBy(1000)
        advanceUntilIdle() // Let the coroutine complete and changes propagate

        Assert.assertTrue(viewModel.uiState.value.article is ArticleDetailUiState.Success)

        val articleInState = (viewModel.uiState.value.article as ArticleDetailUiState.Success).article
        Assert.assertEquals(sampleArticle, articleInState)
    }

    @Test
    fun `uiState reflects error on network failure during article load`() = runTest {
        whenever(repoMock.getById(sampleArticle.id)).thenReturn(
            flow {
                delay(1000) // Simulated network delay
                throw IOException("Network Error") // Simulate network error
            },
        )

        viewModel = ArticleDetailViewModel(
            articleRepository = repoMock,
            savedStateHandle = SavedStateHandle(
                initialState = mapOf(
                    DestinationsArgs.ARTICLE_ID_ARG to sampleArticle.id,
                ),
            ),
        )

        // Create an empty collector for the StateFlow
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        Assert.assertTrue(viewModel.uiState.value.article is ArticleDetailUiState.Loading)

        advanceTimeBy(1001)

        Assert.assertTrue(viewModel.uiState.value.article is ArticleDetailUiState.Error)
    }
}
