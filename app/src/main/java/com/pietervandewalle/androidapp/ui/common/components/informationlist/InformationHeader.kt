package com.pietervandewalle.androidapp.ui.common.components.informationlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.pietervandewalle.androidapp.R

@Composable
fun InformationHeader(modifier: Modifier = Modifier, title: String, content: @Composable () -> Unit) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
        )
        content()
    }
}
