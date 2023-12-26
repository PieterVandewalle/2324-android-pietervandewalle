package com.pietervandewalle.androidapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.pietervandewalle.androidapp.ui.AndroidApp
import com.pietervandewalle.androidapp.ui.theme.AndroidAppTheme


/**
 * This is the main activity for the application.
 * It extends [ComponentActivity] and is responsible for initializing the user interface.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    AndroidApp()
                }
            }
        }
    }
}
