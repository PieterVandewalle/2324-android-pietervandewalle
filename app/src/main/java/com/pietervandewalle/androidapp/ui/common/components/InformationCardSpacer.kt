package com.pietervandewalle.androidapp.ui.common.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InformationCardSpacer(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.height(16.dp))
}