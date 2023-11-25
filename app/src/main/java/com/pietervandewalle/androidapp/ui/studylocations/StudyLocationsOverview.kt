package com.pietervandewalle.androidapp.ui.studylocations

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pietervandewalle.androidapp.R
import com.pietervandewalle.androidapp.model.StudyLocation
import com.pietervandewalle.androidapp.ui.common.components.LoadingIndicator
import com.pietervandewalle.androidapp.ui.common.components.PullRefreshContainer
import com.pietervandewalle.androidapp.ui.navigation.MyTopAppBar

@Composable
fun StudyLocationsOverview(modifier: Modifier = Modifier, studyLocationsOverviewViewModel: StudyLocationsOverviewViewModel = viewModel(factory = StudyLocationsOverviewViewModel.Factory)) {
    val studyLocationsOverviewState by studyLocationsOverviewViewModel.uiState.collectAsState()
    val studyLocationsApiState = studyLocationsOverviewViewModel.studyLocationsApiState
    val studyLocationsApiRefreshingState = studyLocationsOverviewViewModel.studyLocationsApiRefreshingState
    val isRefreshing = studyLocationsApiRefreshingState is StudyLocationsApiState.Loading

    Scaffold(
        topBar = {
            MyTopAppBar(screenTitle = R.string.studylocations) {
            }
        },
    ) { innerPadding ->
        PullRefreshContainer(
            isRefreshing = isRefreshing,
            onRefresh = studyLocationsOverviewViewModel::refresh,
            modifier = modifier.padding(innerPadding),
        ) {
            when (studyLocationsApiState) {
                is StudyLocationsApiState.Loading -> LoadingIndicator()
                is StudyLocationsApiState.Error -> Text("Couldn't load...")
                is StudyLocationsApiState.Success ->
                    StudyLocations(
                        studyLocations = studyLocationsOverviewState.studyLocations,
                        onViewDetail = { },
                    )
            }
        }
    }
}

@Composable
fun StudyLocations(modifier: Modifier = Modifier, studyLocations: List<StudyLocation>, onViewDetail: (StudyLocation) -> Unit) {
    val lazyListState = rememberLazyListState()
    LazyColumn(state = lazyListState, modifier = modifier) {
        items(studyLocations) { studyLocation ->
            Text(studyLocation.title)
        }
    }
}
