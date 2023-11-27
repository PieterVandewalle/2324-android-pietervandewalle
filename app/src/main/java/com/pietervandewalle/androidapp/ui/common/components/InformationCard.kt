package com.pietervandewalle.androidapp.ui.common.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.pietervandewalle.androidapp.R

@Composable
fun InformationCard(modifier: Modifier = Modifier, headerTitle: String, headerContent: @Composable () -> Unit, informationListContent: @Composable () -> Unit, bottomContent: @Composable () -> Unit) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.padding_large)),
    ) {
        Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))) {
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
