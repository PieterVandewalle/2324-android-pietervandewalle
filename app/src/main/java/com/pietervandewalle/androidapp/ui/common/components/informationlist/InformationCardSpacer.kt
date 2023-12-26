package com.pietervandewalle.androidapp.ui.common.components.informationlist

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.pietervandewalle.androidapp.R


/**
 * A composable that provides spacing within an [InformationCard].
 *
 * @param modifier The modifier to apply to this composable.
 */
@Composable
fun InformationCardSpacer(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.height(dimensionResource(R.dimen.padding_large)))
}
