package com.pietervandewalle.androidapp.ui.common.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.pietervandewalle.androidapp.R
import com.pietervandewalle.androidapp.ui.LocalSnackbarHostState


/**
 * A composable that displays an error snackbar with a message and an action button.
 *
 * @param isError Whether to display the error snackbar or not.
 * @param onErrorConsumed The callback function to execute when the error is consumed.
 */
@Composable
fun ErrorSnackbar(isError: Boolean, onErrorConsumed: () -> Unit) {
    val snackbarHostState = LocalSnackbarHostState.current
    val context = LocalContext.current
    val errorMessage = context.getString(R.string.error_text)
    val okText = context.getString(R.string.ok)

    if (isError) {
        LaunchedEffect(snackbarHostState) {
            snackbarHostState.showSnackbar(
                message = errorMessage,
                actionLabel = okText,
            )
            onErrorConsumed()
        }
    }
}
