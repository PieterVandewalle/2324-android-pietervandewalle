package com.pietervandewalle.androidapp.ui.common.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.dimensionResource
import com.pietervandewalle.androidapp.R
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicator
import eu.bambooapps.material3.pullrefresh.pullRefresh
import eu.bambooapps.material3.pullrefresh.rememberPullRefreshState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun PullRefreshContainer(topAppBarState: TopAppBarState, isRefreshing: Boolean, onRefresh: () -> Unit, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val pullRefreshState = rememberPullRefreshState(isRefreshing, onRefresh = onRefresh)

    val contentAlpha: Float by animateFloatAsState(
        targetValue = if (isRefreshing || pullRefreshState.progress > 0.5) 0.2f else 1f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = LinearEasing,
        ),
        label = "contentAlpha",
    )

    // Pullrefresh interferes with the collapsing topAppBar, making it impossible to scroll to the top and make the topAppBar visible again
    // This happens only when testing in the emulator
    // On a real device this problem does not occur, as the pullRefresh gesture is not activated when scrolling
    var isScrolledToTop by remember { mutableStateOf(true) }

    LaunchedEffect(topAppBarState) {
        snapshotFlow { topAppBarState.collapsedFraction }
            .debounce(300)
            .collect { fraction ->
                isScrolledToTop = fraction == -0f
            }
    }

    Box(modifier = modifier.fillMaxSize().pullRefresh(pullRefreshState, enabled = isScrolledToTop)) {
        Box(modifier = Modifier.alpha(contentAlpha)) {
            content()
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.padding(top = dimensionResource(id = R.dimen.padding_extra_small)).align(Alignment.TopCenter),
        )
    }
}
