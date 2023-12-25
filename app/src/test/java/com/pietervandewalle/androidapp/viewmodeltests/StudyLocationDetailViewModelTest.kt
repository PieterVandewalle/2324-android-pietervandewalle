package com.pietervandewalle.androidapp.viewmodeltests

import androidx.lifecycle.SavedStateHandle
import com.pietervandewalle.androidapp.TestDispatcherRule
import com.pietervandewalle.androidapp.data.repo.CachingStudyLocationRepository
import com.pietervandewalle.androidapp.data.sampler.StudyLocationSampler
import com.pietervandewalle.androidapp.ui.navigation.DestinationsArgs
import com.pietervandewalle.androidapp.ui.studylocations.detail.StudyLocationDetailViewModel
import com.pietervandewalle.androidapp.ui.studylocations.detail.StudyLocationUiState
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
class StudyLocationDetailViewModelTest {
    @get:Rule
    val testDispatcherRule = TestDispatcherRule()

    private lateinit var viewModel: StudyLocationDetailViewModel

    private val repoMock = mock<CachingStudyLocationRepository>()

    private val sampleStudyLocation = StudyLocationSampler.getAll().first()

    @Test
    fun `uiState reflects success and contains studyLocation on successful load`() = runTest {
        whenever(repoMock.getById(sampleStudyLocation.id)).thenReturn(
            flow {
                delay(1000) // Simulated network delay
                emit(sampleStudyLocation)
            },
        )

        viewModel = StudyLocationDetailViewModel(
            studyLocationRepository = repoMock,
            savedStateHandle = SavedStateHandle(
                initialState = mapOf(
                    DestinationsArgs.STUDYLOCATION_ID_ARG to sampleStudyLocation.id,
                ),
            ),
        )

        // Create an empty collector for the StateFlow
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        Assert.assertTrue(viewModel.uiState.value.studyLocation is StudyLocationUiState.Loading)

        advanceTimeBy(1000)
        advanceUntilIdle() // Let the coroutine complete and changes propagate

        Assert.assertTrue(viewModel.uiState.value.studyLocation is StudyLocationUiState.Success)

        val studyLocationInState = (viewModel.uiState.value.studyLocation as StudyLocationUiState.Success).studyLocation
        Assert.assertEquals(sampleStudyLocation, studyLocationInState)
    }

    @Test
    fun `uiState reflects error on network failure during studyLocation load`() = runTest {
        whenever(repoMock.getById(sampleStudyLocation.id)).thenReturn(
            flow {
                delay(1000) // Simulated network delay
                throw IOException("Network Error") // Simulate network error
            },
        )

        viewModel = StudyLocationDetailViewModel(
            studyLocationRepository = repoMock,
            savedStateHandle = SavedStateHandle(
                initialState = mapOf(
                    DestinationsArgs.STUDYLOCATION_ID_ARG to sampleStudyLocation.id,
                ),
            ),
        )

        // Create an empty collector for the StateFlow
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        Assert.assertTrue(viewModel.uiState.value.studyLocation is StudyLocationUiState.Loading)

        advanceTimeBy(1001)

        Assert.assertTrue(viewModel.uiState.value.studyLocation is StudyLocationUiState.Error)
    }
}
