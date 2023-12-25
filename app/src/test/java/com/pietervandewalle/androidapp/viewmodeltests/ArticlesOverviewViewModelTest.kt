package com.pietervandewalle.androidapp.viewmodeltests

import com.pietervandewalle.androidapp.TestDispatcherRule
import com.pietervandewalle.androidapp.data.repo.CachingArticleRepository
import com.pietervandewalle.androidapp.data.sampler.ArticleSampler
import com.pietervandewalle.androidapp.model.Article
import com.pietervandewalle.androidapp.ui.articles.overview.ArticleOverviewViewModel
import com.pietervandewalle.androidapp.ui.articles.overview.ArticlesUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
class ArticlesOverviewViewModelTest {
    @get:Rule
    val testDispatcherRule = TestDispatcherRule()

    private lateinit var viewModel: ArticleOverviewViewModel

    private val repoMock = mock<CachingArticleRepository>()

    @Test
    fun `uiState reflects success and contains articles on successful articles load`() = runTest {
        whenever(repoMock.getAll()).thenReturn(
            flow {
                delay(1000) // Simulated network delay
                emit(ArticleSampler.getAll())
            },
        )

        viewModel = ArticleOverviewViewModel(articleRepository = repoMock)

        // Create an empty collector for the StateFlow
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        Assert.assertTrue(viewModel.uiState.value.articles is ArticlesUiState.Loading)

        advanceTimeBy(1000)
        advanceUntilIdle() // Let the coroutine complete and changes propagate

        Assert.assertTrue(viewModel.uiState.value.articles is ArticlesUiState.Success)

        val articlesInState = (viewModel.uiState.value.articles as ArticlesUiState.Success).articles
        Assert.assertEquals(ArticleSampler.getAll(), articlesInState)
    }

    @Test
    fun `uiState reflects error on network failure during articles load`() = runTest {
        // Simulate network error
        whenever(repoMock.getAll()).thenReturn(
            flow {
                delay(1000) // Simulated network delay
                throw IOException("Network Error") // Simulate network error
            },
        )

        viewModel = ArticleOverviewViewModel(articleRepository = repoMock)

        // Create an empty collector for the StateFlow
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        Assert.assertTrue(viewModel.uiState.value.articles is ArticlesUiState.Loading)

        advanceTimeBy(1001)

        Assert.assertTrue(viewModel.uiState.value.articles is ArticlesUiState.Error)
    }

    @Test
    fun `onErrorConsumed resets error state after refresh error`() = runTest {
        whenever(repoMock.getAll()).thenReturn(
            flow {
                emit(ArticleSampler.getAll())
            },
        )
        // Set up repoMock to throw an exception on refresh
        whenever(repoMock.refresh()).thenAnswer { throw IOException("Network Error") }

        viewModel = ArticleOverviewViewModel(articleRepository = repoMock)

        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        // Call refresh which should throw an exception and set isError to true
        viewModel.refresh()

        advanceUntilIdle()

        // The isError state should be true after the refresh error
        val errorStateAfterRefresh = viewModel.uiState.value.isError
        Assert.assertTrue(errorStateAfterRefresh)

        // Reset error
        viewModel.onErrorConsumed()

        advanceUntilIdle()

        // The error state should be reset to false
        val errorStateAfterReset = viewModel.uiState.value.isError
        Assert.assertFalse(errorStateAfterReset)
    }

    @Test
    fun `refresh resets error state after refresh error`() = runTest {
        whenever(repoMock.getAll()).thenReturn(
            flow {
                emit(ArticleSampler.getAll())
            },
        )
        // Set up repoMock to throw an exception on refresh
        whenever(repoMock.refresh()).thenAnswer { throw IOException("Network Error") }

        viewModel = ArticleOverviewViewModel(articleRepository = repoMock)

        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        // Call refresh which should throw an exception and set isError to true
        viewModel.refresh()

        advanceUntilIdle()

        // The isError state should be true after the refresh error
        val errorStateAfterRefresh = viewModel.uiState.value.isError
        Assert.assertTrue(errorStateAfterRefresh)

        // Reset error
        viewModel.onErrorConsumed()

        advanceUntilIdle()

        // The error state should be reset to false
        val errorStateAfterReset = viewModel.uiState.value.isError
        Assert.assertFalse(errorStateAfterReset)
    }

    @Test
    fun `refresh updates articles in uiState`() = runTest {
        val articlesFlow = MutableStateFlow<List<Article>>(listOf(ArticleSampler.getAll().first()))

        whenever(repoMock.getAll()).thenReturn(
            articlesFlow,
        )

        whenever(repoMock.refresh()).thenAnswer {
            // Simulate the behavior of updating studyLocations
            runBlocking {
                articlesFlow.emit(ArticleSampler.getAll())
            }
            Unit
        }

        viewModel = ArticleOverviewViewModel(articleRepository = repoMock)

        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()
        val initialArticlesInState = (viewModel.uiState.value.articles as ArticlesUiState.Success).articles

        Assert.assertEquals(1, initialArticlesInState.size)

        viewModel.refresh()

        advanceUntilIdle()

        val updatedArticlesInState = (viewModel.uiState.value.articles as ArticlesUiState.Success).articles
        Assert.assertEquals(ArticleSampler.getAll().size, updatedArticlesInState.size)
    }
}
