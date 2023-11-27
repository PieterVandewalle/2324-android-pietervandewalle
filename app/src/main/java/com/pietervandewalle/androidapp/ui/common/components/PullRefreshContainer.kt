package com.pietervandewalle.androidapp.ui.common.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.dimensionResource
import com.pietervandewalle.androidapp.R
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicator
import eu.bambooapps.material3.pullrefresh.pullRefresh
import eu.bambooapps.material3.pullrefresh.rememberPullRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullRefreshContainer(isRefreshing: Boolean, onRefresh: () -> Unit, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val pullRefreshState = rememberPullRefreshState(isRefreshing, onRefresh = onRefresh)

    val contentAlpha: Float by animateFloatAsState(
        targetValue = if (isRefreshing || pullRefreshState.progress > 0.5) 0.2f else 1f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = LinearEasing,
        ),
        label = "contentAlpha",
    )

    Box(modifier = modifier.pullRefresh(pullRefreshState)) {
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
