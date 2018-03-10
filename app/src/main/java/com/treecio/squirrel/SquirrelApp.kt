package com.treecio.squirrel

import android.support.multidex.MultiDexApplication
import android.support.v7.app.AppCompatDelegate
import com.facebook.stetho.Stetho
import timber.log.Timber

class SquirrelApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        // Timber
        Timber.plant(Timber.DebugTree())

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        // Stetho
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
    }

}
