package com.pietervandewalle.androidapp

import android.app.Application
import com.pietervandewalle.androidapp.data.AppContainer
import com.pietervandewalle.androidapp.data.DefaultAppContainer

/**
 * This is the main [Application] class for the Android application.
 * It extends the [Application] class and initializes an instance of [AppContainer]
 * using [DefaultAppContainer] in its [onCreate] method.
 *
 * The [container] property is used to manage dependencies and resources
 * throughout the application's lifecycle.
 */
class AndroidApplication : Application() {

    /**
     * The [AppContainer] instance used for dependency management
     * and resource handling throughout the application's lifecycle.
     * This property is initialized in the [onCreate] method.
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(applicationContext)
    }
}
