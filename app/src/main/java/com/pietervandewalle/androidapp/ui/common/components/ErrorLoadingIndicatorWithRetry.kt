package com.pietervandewalle.androidapp.ui.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.pietervandewalle.androidapp.R

@Composable
fun ErrorLoadingIndicatorWithRetry(modifier: Modifier = Modifier, onRetry: () -> Unit) {
    Column(
        modifier.padding(
            horizontal = dimensionResource(
                R.dimen.padding_large,
            ),
        ),
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.padding_medium),
        ),
    ) {
        Text(stringResource(R.string.loading_failed))
        Button(onClick = onRetry) {
            Text(stringResource(R.string.try_again))
        }
    }
}
