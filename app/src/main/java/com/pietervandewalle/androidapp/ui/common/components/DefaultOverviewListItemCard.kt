package com.pietervandewalle.androidapp.ui.common.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.pietervandewalle.androidapp.R


/**
 * A composable that represents a default overview list item card with elevated styling.
 *
 * @param modifier The modifier to apply to this composable.
 * @param content The composable lambda for the content of the card.
 */
@Composable
fun DefaultOverviewListItemCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    ElevatedCard(
        modifier.padding(dimensionResource(R.dimen.padding_extra_small))
            .fillMaxWidth(),
        shape = RoundedCornerShape(1.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp,
        ),
    ) {
        content()
    }
}
