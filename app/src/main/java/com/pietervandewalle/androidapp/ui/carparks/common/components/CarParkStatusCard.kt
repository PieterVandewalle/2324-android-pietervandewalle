package com.pietervandewalle.androidapp.ui.carparks.common.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pietervandewalle.androidapp.R
import com.pietervandewalle.androidapp.data.sampler.CarParkSampler
import com.pietervandewalle.androidapp.model.CarPark
import com.pietervandewalle.androidapp.model.isAlmostFull
import com.pietervandewalle.androidapp.model.isFull
import com.pietervandewalle.androidapp.ui.theme.AndroidAppTheme
import com.pietervandewalle.androidapp.ui.theme.successContainer
import com.pietervandewalle.androidapp.ui.theme.warningContainer

/**
 * Composable function for displaying the status of a car park in a card.
 *
 * @param carPark The car park for which the status is to be displayed.
 */
@Composable
fun CarParkStatusCard(carPark: CarPark) {
    Card(colors = CardDefaults.cardColors(containerColor = determineStatusColor(carPark = carPark)), shape = RoundedCornerShape(4.dp)) {
        Column(
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_extra_small))
                .width(107.dp),
        ) {
            if (carPark.isOpenNow) {
                CarParkOpenStatusText(carPark = carPark)
            } else {
                CarParkClosedStatusText(carPark = carPark)
            }
        }
    }
}

/**
 * Composable function for displaying the open status of a car park.
 *
 * @param carPark The car park for which the open status is to be displayed.
 */
@Composable
private fun CarParkOpenStatusText(carPark: CarPark) {
    Text(
        when {
            carPark.isFull -> stringResource(R.string.full)
            carPark.isAlmostFull -> stringResource(R.string.almost_full)
            else -> stringResource(R.string.available_space)
        },
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
    )

    if (!carPark.isFull) {
        Text(
            pluralStringResource(R.plurals.numberOfParkingSpotsAvailable, carPark.availableCapacity, carPark.availableCapacity),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

/**
 * Composable function for displaying the closed status text of a car park.
 *
 * @param carPark The car park for which the closed status is to be displayed.
 */
@Composable
private fun CarParkClosedStatusText(carPark: CarPark) {
    Text(
        if (carPark.isTemporaryClosed) {
            stringResource(R.string.temporary_closed)
        } else {
            stringResource(R.string.closed)
        },
        style = MaterialTheme.typography.bodyMedium,
    )
}

/**
 * Determines the color for the car park status based on its open and capacity status.
 *
 * @param carPark The car park for which the status color is determined.
 * @return The color for the car park status.
 */
@Composable
fun determineStatusColor(carPark: CarPark): Color {
    if (!carPark.isOpenNow || carPark.isFull) {
        return MaterialTheme.colorScheme.errorContainer
    }

    if (carPark.isAlmostFull) {
        return MaterialTheme.colorScheme.warningContainer
    }
    return MaterialTheme.colorScheme.successContainer
}

// Previews
@Preview
@Composable
private fun CarParkStatusCardPreviewFree() {
    AndroidAppTheme {
        CarParkStatusCard(carPark = CarParkSampler.getOneNotFull())
    }
}

@Preview
@Composable
private fun CarParkStatusCardPreviewClosed() {
    AndroidAppTheme {
        CarParkStatusCard(carPark = CarParkSampler.getOneTemporaryClosed())
    }
}

@Preview
@Composable
private fun CarParkStatusCardPreviewFull() {
    AndroidAppTheme {
        CarParkStatusCard(carPark = CarParkSampler.getOneFull())
    }
}

@Preview
@Composable
private fun CarParkStatusCardPreviewAlmostFull() {
    AndroidAppTheme {
        CarParkStatusCard(carPark = CarParkSampler.getOneAlmostFull())
    }
}
