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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.pietervandewalle.androidapp.R
import com.pietervandewalle.androidapp.model.StudyLocation
import com.pietervandewalle.androidapp.ui.common.components.DefaultOverviewListItemCard
import com.pietervandewalle.androidapp.ui.common.components.ErrorLoadingIndicatorWithRetry
import com.pietervandewalle.androidapp.ui.common.components.ErrorSnackbar
import com.pietervandewalle.androidapp.ui.common.components.LoadingIndicator
import com.pietervandewalle.androidapp.ui.common.components.PullRefreshContainer
import com.pietervandewalle.androidapp.ui.navigation.MyTopAppBar
import com.pietervandewalle.androidapp.ui.studylocations.components.ClickOutOfSearchBox
import com.pietervandewalle.androidapp.ui.studylocations.components.MySearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyLocationsOverview(modifier: Modifier = Modifier, onNavigateToDetail: (Int) -> Unit, studyLocationsOverviewViewModel: StudyLocationsOverviewViewModel = viewModel(factory = StudyLocationsOverviewViewModel.Factory)) {
    val uiState by studyLocationsOverviewViewModel.uiState.collectAsState()
    val studyLocationsUiState = uiState.studyLocations

    ErrorSnackbar(isError = uiState.isError, onErrorConsumed = studyLocationsOverviewViewModel::onErrorConsumed)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = !uiState.isSearchOpen,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                MyTopAppBar(screenTitle = R.string.studylocations, scrollBehavior = scrollBehavior) {
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
                    searchterm = uiState.currentSearchTerm,
                    onSearchtermChange = studyLocationsOverviewViewModel::updateSearchTerm,
                    onCancel = studyLocationsOverviewViewModel::closeSearch,
                    onSearch = studyLocationsOverviewViewModel::search,
                )
            }
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        PullRefreshContainer(
            topAppBarState = scrollBehavior.state,
            isRefreshing = uiState.isRefreshing,
            onRefresh = studyLocationsOverviewViewModel::refresh,
            modifier = Modifier
                .padding(innerPadding),
        ) {
            when (studyLocationsUiState) {
                is StudyLocationsUiState.Loading -> LoadingIndicator()
                is StudyLocationsUiState.Error -> ErrorLoadingIndicatorWithRetry(onRetry = studyLocationsOverviewViewModel::refresh)
                is StudyLocationsUiState.Success ->
                    Column(modifier = Modifier.padding(5.dp)) {
                        if (uiState.areResultsFiltered) {
                            SearchResult(hasMatches = studyLocationsUiState.studyLocations.isNotEmpty(), searchterm = uiState.completedSearchTerm, onReset = studyLocationsOverviewViewModel::resetSearch)
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

@Composable
fun SearchResult(hasMatches: Boolean, searchterm: String, onReset: () -> Unit) {
    Column {
        Text(
            "${if (!hasMatches) "Geen resultaten" else "Resultaten"} voor zoekopdracht:\n'$searchterm'",
            modifier = Modifier.padding(horizontal = 10.dp),
        )
        TextButton(onClick = onReset) {
            Text(
                "Verwijder zoekopdracht",
            )
        }
    }
}

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
                                modifier = modifier.fillMaxWidth(0.5f), studyLocation = studyLocations[index],
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
    }
}

@Composable
fun StudyLocationListItem(modifier: Modifier = Modifier, studyLocation: StudyLocation, onViewDetail: () -> Unit) {
    DefaultOverviewListItemCard(
        modifier = modifier.clickable { onViewDetail() },
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(modifier = Modifier.height(150.dp)) {
                AsyncImage(
                    model = studyLocation.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                Card(modifier = Modifier.padding(10.dp), shape = RoundedCornerShape(2.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                    Text(studyLocation.label, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(5.dp))
                }
            }
            Text(studyLocation.title, style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = "location",
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(studyLocation.address, style = MaterialTheme.typography.bodyMedium)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Groups,
                    contentDescription = "capacity",
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(studyLocation.totalCapacity.toString(), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
