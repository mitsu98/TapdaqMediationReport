/*
 * Initialize.kt
 * Copyright 2020 Axl Yody <axlyod@gmail.com>
 *
 */

package id.axlyody.tapdaqmediationreport

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import timber.log.Timber

class Initialize : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseAnalytics.getInstance(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
