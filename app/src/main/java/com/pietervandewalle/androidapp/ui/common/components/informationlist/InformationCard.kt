package com.pietervandewalle.androidapp.ui.common.components.informationlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.Plagiarism
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.pietervandewalle.androidapp.R
import com.pietervandewalle.androidapp.ui.theme.AndroidAppTheme

@Composable
fun InformationCard(modifier: Modifier = Modifier, headerTitle: String, headerContent: @Composable () -> Unit, informationListContent: @Composable () -> Unit, bottomContent: @Composable () -> Unit) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.padding_large)),
    ) {
        Column(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))) {
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

@Composable
@Preview(showBackground = true)
private fun InformationCardPreview() {
    AndroidAppTheme {
        InformationCard(
            headerTitle = "Dit is een information card", headerContent = {
                Text(text = "Dit is de header content van een information card")
            }, informationListContent = {
                InformationListItem(icon = Icons.Filled.Cabin, label = "Label1", value = "Value1")
                InformationListItem(icon = Icons.Filled.Plagiarism, label = "Label2", value = "Value2")
            },
        ) {
            Text(text = "Dit is de bottom content")
        }
    }
}
