package com.dgmpi.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DgmpiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
