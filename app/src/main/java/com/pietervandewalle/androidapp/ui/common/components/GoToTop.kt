package com.pietervandewalle.androidapp.ui.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pietervandewalle.androidapp.R
import kotlinx.coroutines.launch


/**
 * Composable function to display a "Go to Top" button that allows the user to scroll to the top
 * of a LazyColumn when clicked.
 *
 * @param goToTop A lambda function to execute when the button is clicked.
 */
@Composable
fun GoToTop(goToTop: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        FloatingActionButton(
            modifier = Modifier
                .padding(16.dp)
                .size(50.dp)
                .align(Alignment.BottomEnd),
            onClick = goToTop,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowUpward,
                contentDescription = stringResource(R.string.scroll_to_top),
            )
        }
    }
}


/**
 * Composable function to display a "Scroll to Top" button when the user is scrolling down in a
 * LazyColumn. When clicked, it scrolls the list to the top.
 *
 * @param lazyListState The [LazyListState] used to manage the scroll state of the LazyColumn.
 */
@Composable
fun ScrollToTopButton(lazyListState: LazyListState) {
    val scope = rememberCoroutineScope()

    AnimatedVisibility(
        visible = !lazyListState.isScrollingUp(),
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        GoToTop {
            scope.launch {
                lazyListState.scrollToItem(0)
            }
        }
    }
}

/**
 * Extension function for [LazyListState] to determine if the list is currently scrolling up.
 *
 * @return `true` if the list is scrolling up; `false` otherwise.
 */
@Composable
fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}
