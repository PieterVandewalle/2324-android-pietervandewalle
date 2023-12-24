package com.pietervandewalle.androidapp.ui.studylocations.overview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.pietervandewalle.androidapp.R
import com.pietervandewalle.androidapp.data.sampler.StudyLocationSampler
import com.pietervandewalle.androidapp.model.StudyLocation
import com.pietervandewalle.androidapp.ui.common.components.DefaultOverviewListItemCard
import com.pietervandewalle.androidapp.ui.common.components.ErrorLoadingIndicatorWithRetry
import com.pietervandewalle.androidapp.ui.common.components.ErrorSnackbar
import com.pietervandewalle.androidapp.ui.common.components.LoadingIndicator
import com.pietervandewalle.androidapp.ui.common.components.PullRefreshContainer
import com.pietervandewalle.androidapp.ui.common.components.ScrollToTopButton
import com.pietervandewalle.androidapp.ui.navigation.MyTopAppBar
import com.pietervandewalle.androidapp.ui.studylocations.components.ClickOutOfSearchBox
import com.pietervandewalle.androidapp.ui.studylocations.components.MySearchBar
import com.pietervandewalle.androidapp.ui.theme.AndroidAppTheme

/**
 * Composable function for displaying the Study Locations Overview screen.
 *
 * @param modifier The modifier for this composable.
 * @param onNavigateToDetail Callback to navigate to the detail screen.
 * @param studyLocationsOverviewViewModel The ViewModel for study locations overview.
 */
@Composable
fun StudyLocationsOverview(modifier: Modifier = Modifier, onNavigateToDetail: (Int) -> Unit, studyLocationsOverviewViewModel: StudyLocationsOverviewViewModel = viewModel(factory = StudyLocationsOverviewViewModel.Factory)) {
    val uiState by studyLocationsOverviewViewModel.uiState.collectAsState()
    val studyLocationsUiState = uiState.studyLocations

    ErrorSnackbar(isError = uiState.isError, onErrorConsumed = studyLocationsOverviewViewModel::onErrorConsumed)
    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = !uiState.isSearchOpen,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                MyTopAppBar(screenTitle = R.string.studylocations) {
                    IconButton(onClick = studyLocationsOverviewViewModel::openSearch) {
                        Icon(Icons.Filled.Search, contentDescription = null)
                    }
                }
            }

            AnimatedVisibility(
                visible = uiState.isSearchOpen,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                MySearchBar(
                    placeholder = stringResource(id = R.string.search_studylocations),
                    searchTerm = uiState.currentSearchTerm,
                    onSearchTermChange = studyLocationsOverviewViewModel::updateSearchTerm,
                    onCancel = studyLocationsOverviewViewModel::closeSearch,
                    onSearch = studyLocationsOverviewViewModel::search,
                )
            }
        },
        modifier = modifier,
    ) { innerPadding ->
        PullRefreshContainer(
            isRefreshing = uiState.isRefreshing,
            onRefresh = studyLocationsOverviewViewModel::refresh,
            modifier = Modifier
                .padding(innerPadding),
        ) {
            when (studyLocationsUiState) {
                is StudyLocationsUiState.Loading -> LoadingIndicator()
                is StudyLocationsUiState.Error -> ErrorLoadingIndicatorWithRetry(onRetry = studyLocationsOverviewViewModel::refresh)
                is StudyLocationsUiState.Success ->
                    Column(modifier = Modifier.padding(dimensionResource(R.dimen.padding_extra_small))) {
                        if (uiState.areResultsFiltered) {
                            SearchResult(hasMatches = studyLocationsUiState.studyLocations.isNotEmpty(), searchTerm = uiState.completedSearchTerm, onReset = studyLocationsOverviewViewModel::resetSearch)
                        }
                        StudyLocations(
                            studyLocations = studyLocationsUiState.studyLocations,
                            onViewDetail = { onNavigateToDetail(it.id) },
                        )
                    }
            }
            ClickOutOfSearchBox(
                isSearchOpen = uiState.isSearchOpen,
                closeSearch = studyLocationsOverviewViewModel::closeSearch,
            )
        }
    }
}

/**
 * Composable function for displaying the search result.
 *
 * @param hasMatches Whether there are matches for the search term.
 * @param searchTerm The current search term.
 * @param onReset Callback to reset the search.
 */
@Composable
fun SearchResult(hasMatches: Boolean, searchTerm: String, onReset: () -> Unit) {
    Column {
        Text(
            stringResource(
                R.string.for_searchTerm_x,
                if (!hasMatches) {
                    stringResource(R.string.search_no_results)
                } else {
                    stringResource(
                        R.string.results,
                    )
                },
                searchTerm,
            ),
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_medium)),
        )
        TextButton(onClick = onReset) {
            Text(
                stringResource(R.string.remove_searchTerm),
            )
        }
    }
}

/**
 * Composable function for displaying the list of study locations.
 *
 * @param modifier The modifier for this composable.
 * @param studyLocations List of study locations to display.
 * @param onViewDetail Callback to view the detail of a study location.
 */
@Composable
fun StudyLocations(modifier: Modifier = Modifier, studyLocations: List<StudyLocation>, onViewDetail: (StudyLocation) -> Unit) {
    val lazyListState = rememberLazyListState()

    BoxWithConstraints(modifier = modifier) {
        if (maxWidth < 1200.dp) {
            LazyColumn(state = lazyListState) {
                items(studyLocations) { studyLocation ->
                    StudyLocationListItem(
                        onViewDetail = { onViewDetail(studyLocation) },
                        studyLocation = studyLocation,
                    )
                }
            }
        } else {
            // 2 studyLocations per row on large screens
            LazyColumn(state = lazyListState) {
                for (index in studyLocations.indices step 2) {
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))) {
                            StudyLocationListItem(
                                modifier = Modifier.fillMaxWidth(0.5f),
                                studyLocation = studyLocations[index],
                                onViewDetail = { onViewDetail(studyLocations[index]) },
                            )
                            if (index + 1 != studyLocations.size) {
                                StudyLocationListItem(
                                    studyLocation = studyLocations[index + 1],
                                    onViewDetail = { onViewDetail(studyLocations[index + 1]) },
                                )
                            }
                        }
                    }
                }
            }
        }
        ScrollToTopButton(lazyListState = lazyListState)
    }
}

/**
 * Composable function for displaying a single study location item.
 *
 * @param modifier The modifier for this composable.
 * @param studyLocation The study location to display.
 * @param onViewDetail Callback to view the detail of the study location.
 */
@Composable
fun StudyLocationListItem(modifier: Modifier = Modifier, studyLocation: StudyLocation, onViewDetail: () -> Unit) {
    DefaultOverviewListItemCard(
        modifier = modifier.clickable { onViewDetail() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
        ) {
            Box(modifier = Modifier.height(dimensionResource(R.dimen.image_container_medium))) {
                AsyncImage(
                    model = studyLocation.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                Card(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)), shape = RoundedCornerShape(2.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                    Text(
                        studyLocation.label,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(dimensionResource(R.dimen.padding_extra_small)),
                    )
                }
            }
            Text(studyLocation.title, style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = stringResource(R.string.location),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(studyLocation.address, style = MaterialTheme.typography.bodyMedium)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Groups,
                    contentDescription = stringResource(R.string.capacity),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(studyLocation.totalCapacity.toString(), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun StudyLocationListItemPreview() {
    AndroidAppTheme {
        StudyLocationListItem(studyLocation = StudyLocationSampler.getAll().first(), onViewDetail = {})
    }
}

@Composable
@Preview(showBackground = true)
private fun SearchResultNoMatchesPreview() {
    AndroidAppTheme {
        SearchResult(hasMatches = false, searchTerm = "Schoonmeersen", onReset = {})
    }
}
