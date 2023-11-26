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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pietervandewalle.androidapp.R
import com.pietervandewalle.androidapp.model.CarPark
import com.pietervandewalle.androidapp.model.isAlmostFull
import com.pietervandewalle.androidapp.model.isFull
import com.pietervandewalle.androidapp.ui.theme.successContainer
import com.pietervandewalle.androidapp.ui.theme.warningContainer

@Composable
fun CarParkStatusCard(carPark: CarPark) {
    Card(colors = CardDefaults.cardColors(containerColor = determineStatusColor(carPark = carPark)), shape = RoundedCornerShape(4.dp)) {
        Column(
            modifier = Modifier
                .padding(5.dp)
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

@Composable
private fun CarParkClosedStatusText(carPark: CarPark) {
    Text(
        if (carPark.isTemporaryClosed) "Tijdelijk gesloten" else "Gesloten",
        style = MaterialTheme.typography.bodyMedium,
    )
}

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