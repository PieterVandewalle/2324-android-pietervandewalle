package com.pietervandewalle.androidapp.ui.common.modifiers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed


/**
 * A custom [Modifier] that makes a composable clickable without applying any ripple effect.
 *
 * @param onClick The lambda function to execute when the composable is clicked.
 * @return A [Modifier] that makes the composable clickable without ripple effect.
 */
fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
    ) {
        onClick()
    }
}
