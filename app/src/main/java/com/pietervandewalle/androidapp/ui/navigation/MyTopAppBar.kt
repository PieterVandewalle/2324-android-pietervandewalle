package com.pietervandewalle.androidapp.ui.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(screenTitle: Int, actions: @Composable() RowScope.() -> Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(stringResource(id = screenTitle))
        },
        actions = actions,
    )
}
