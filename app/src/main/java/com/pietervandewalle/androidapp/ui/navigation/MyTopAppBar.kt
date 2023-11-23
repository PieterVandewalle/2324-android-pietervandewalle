package com.pietervandewalle.androidapp.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(screenTitle: Int) {
    TopAppBar(
        title = {
            Text(stringResource(id = screenTitle))
        },
    )
}
