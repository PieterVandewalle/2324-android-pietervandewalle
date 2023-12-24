package com.pietervandewalle.androidapp.ui.studylocations.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import com.pietervandewalle.androidapp.ui.common.modifiers.noRippleClickable

/**
 * Composable function for handling clicks outside of the search box to close the search.
 *
 * @param isSearchOpen True if the search box is open, false otherwise.
 * @param closeSearch Callback to close the search.
 */
@Composable
fun ClickOutOfSearchBox(isSearchOpen: Boolean, closeSearch: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().noRippleClickable { closeSearch() }
            .zIndex(if (isSearchOpen) 1f else -1f),
    )
}
