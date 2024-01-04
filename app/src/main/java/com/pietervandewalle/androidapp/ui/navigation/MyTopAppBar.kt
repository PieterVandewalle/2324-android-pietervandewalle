package com.pietervandewalle.androidapp.ui.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.pietervandewalle.androidapp.R

/**
 * Composable function that displays a customized top app bar.
 *
 * @param screenTitle The resource ID of the title to display in the app bar.
 * @param canNavigateBack A boolean indicating whether a navigation back button should be displayed.
 * @param onNavigateBack A lambda function to execute when the navigation back button is clicked.
 * @param actions A composable lambda to define additional actions or content for the app bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(screenTitle: Int, canNavigateBack: Boolean = false, onNavigateBack: (() -> Unit)? = null, actions: @Composable RowScope.() -> Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(stringResource(id = screenTitle), modifier = Modifier.testTag("topAppBarTitle"))
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = onNavigateBack!!) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.navigate_back),
                    )
                }
            }
        },
        actions = actions,
    )
}
