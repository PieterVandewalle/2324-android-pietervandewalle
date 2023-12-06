package com.pietervandewalle.androidapp

import android.app.Application
import com.pietervandewalle.androidapp.data.AppContainer
import com.pietervandewalle.androidapp.data.DefaultAppContainer

class AndroidApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(applicationContext)
    }
}
