package com.pietervandewalle.androidapp.ui.studylocations

import android.app.appsearch.SearchResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.pietervandewalle.androidapp.R
import com.pietervandewalle.androidapp.model.StudyLocation
import com.pietervandewalle.androidapp.ui.common.components.LoadingIndicator
import com.pietervandewalle.androidapp.ui.common.components.PullRefreshContainer
import com.pietervandewalle.androidapp.ui.navigation.MyTopAppBar
import com.pietervandewalle.androidapp.ui.studylocations.components.ClickOutOfSearchBox
import com.pietervandewalle.androidapp.ui.studylocations.components.MySearchBar

@Composable
fun StudyLocationsOverview(modifier: Modifier = Modifier, studyLocationsOverviewViewModel: StudyLocationsOverviewViewModel = viewModel(factory = StudyLocationsOverviewViewModel.Factory)) {
    val studyLocationsOverviewState by studyLocationsOverviewViewModel.uiState.collectAsState()
    val studyLocationsApiState = studyLocationsOverviewViewModel.studyLocationsApiState
    val studyLocationsApiRefreshingState = studyLocationsOverviewViewModel.studyLocationsApiRefreshingState
    val isRefreshing = studyLocationsApiRefreshingState is StudyLocationsApiState.Loading
    Scaffold(
        topBar = {
            if (!studyLocationsOverviewState.isSearchOpen) {
                MyTopAppBar(screenTitle = R.string.studylocations) {
                    IconButton(onClick = studyLocationsOverviewViewModel::openSearch) {
                        Icon(Icons.Filled.Search, contentDescription = null)
                    }
                }
            } else {
                MySearchBar(
                    placeholder = stringResource(id = R.string.search_studylocations),
                    searchterm = studyLocationsOverviewState.currentSearchterm,
                    onSearchtermChange = studyLocationsOverviewViewModel::updateSearchterm,
                    onCancel = studyLocationsOverviewViewModel::closeSearch,
                    onSearch = studyLocationsOverviewViewModel::search,
                )
            }
        },
    ) { innerPadding ->
        PullRefreshContainer(
            isRefreshing = isRefreshing,
            onRefresh = studyLocationsOverviewViewModel::refresh,
            modifier = modifier
                .padding(innerPadding),
        ) {
            when (studyLocationsApiState) {
                is StudyLocationsApiState.Loading -> LoadingIndicator()
                is StudyLocationsApiState.Error -> Text("Couldn't load...")
                is StudyLocationsApiState.Success ->
                    Column(modifier = Modifier.padding(5.dp)) {
                        if (studyLocationsOverviewState.areResultsFiltered) {
                            SearchResult(hasMatches = studyLocationsOverviewState.studyLocations.isNotEmpty(), searchterm = studyLocationsOverviewState.completedSearchterm, onReset = studyLocationsOverviewViewModel::resetSearch) 
                        }
                        StudyLocations(
                            studyLocations = studyLocationsOverviewState.studyLocations,
                            onViewDetail = { },
                        )
                    }
            }
            ClickOutOfSearchBox(
                isSearchOpen = studyLocationsOverviewState.isSearchOpen,
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
    LazyColumn(state = lazyListState, modifier = modifier.fillMaxSize()) {
        items(studyLocations) { studyLocation ->
            ElevatedCard(
                Modifier
                    .padding(5.dp).clickable { onViewDetail(studyLocation) }
                    .fillMaxWidth(),
                shape = RoundedCornerShape(1.dp),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 2.dp,
                ),
            ) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
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
    }
}
