package com.pietervandewalle.androidapp.ui.common.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InformationCard(modifier: Modifier = Modifier, headerTitle: String, headerContent: @Composable () -> Unit, informationListContent: @Composable () -> Unit, bottomContent: @Composable () -> Unit) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp), // TODO use dimension resource?
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            InformationHeader(title = headerTitle) {
                headerContent()
            }

            InformationCardSpacer()

            InformationList {
                informationListContent()
            }

            InformationCardSpacer()

            bottomContent()
        }
    }
}
