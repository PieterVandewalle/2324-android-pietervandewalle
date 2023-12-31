package com.pietervandewalle.androidapp.ui.common.helpers // ktlint-disable filename

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

/**
 * Converts a vector drawable resource into a [BitmapDescriptor] with the specified color tint.
 *
 * @param context The Android application context.
 * @param vectorResId The resource ID of the vector drawable to be converted.
 * @param color The color tint to apply to the vector drawable.
 * @return A [BitmapDescriptor] with the vector drawable rendered as a bitmap with the given tint,
 *         or `null` if the drawable cannot be loaded.
 */
@Composable
fun bitmapDescriptorFromVector(
    context: Context,
    vectorResId: Int,
    color: Color,
): BitmapDescriptor? {
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    drawable.setTint(color.hashCode())
    val bm = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888,
    )

    // draw it onto the bitmap
    val canvas = Canvas(bm)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bm)
}
