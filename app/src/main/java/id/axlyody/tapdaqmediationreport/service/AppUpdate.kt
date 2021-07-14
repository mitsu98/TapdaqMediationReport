/*
 * AppUpdate.kt
 * Copyright 2020 Axl Yody <axlyod@gmail.com>
 *
 */

package id.axlyody.tapdaqmediationreport.service

import android.app.Activity
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import timber.log.Timber
import java.util.concurrent.Executors

class AppUpdate(
    private var activity: Activity,
) {
    private val appUpdateManager by lazy {
        AppUpdateManagerFactory.create(activity)
    }
    private val executor by lazy {
        Executors.newSingleThreadExecutor()
    }

    private lateinit var installStateUpdatedListener: InstallStateUpdatedListener

    companion object {
        const val REQUEST_UPDATE_CODE = 1
    }

    fun activityResult(resultCode: Int) {
        if (resultCode == REQUEST_UPDATE_CODE) {
            if (resultCode != -1) {
                Timber.e("Update flow failed! Result code: $resultCode")
            }
        }

    }

    fun update(layout: View) {
        installStateUpdatedListener = InstallStateUpdatedListener { installState ->
            when (installState.installStatus()) {
                InstallStatus.DOWNLOADED -> {
                    Timber.d("Downloaded")
                    Snackbar.make(
                        layout,
                        "An update has just been downloaded.",
                        Snackbar.LENGTH_INDEFINITE
                    ).apply {
                        setAction("RESTART") { appUpdateManager.completeUpdate() }
                        show()
                    }
                }
                InstallStatus.INSTALLED -> {
                    Timber.d("Installed")
                    appUpdateManager.unregisterListener(installStateUpdatedListener)
                }
                else -> {
                    Timber.d("installStatus = %s", installState.installStatus())
                }
            }
        }

        appUpdateManager.registerListener(installStateUpdatedListener)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener(executor, { appUpdateInfo ->
            when (appUpdateInfo.updateAvailability()) {
                UpdateAvailability.UPDATE_AVAILABLE -> {
                    val updateTypes = arrayOf(AppUpdateType.FLEXIBLE, AppUpdateType.IMMEDIATE)
                    run loop@{
                        updateTypes.forEach { type ->
                            if (appUpdateInfo.isUpdateTypeAllowed(type)) {
                                appUpdateManager.startUpdateFlowForResult(
                                    appUpdateInfo,
                                    type,
                                    activity,
                                    200
                                )
                                return@loop
                            }
                        }
                    }
                }
                else -> {
                    Timber.d("updateAvailability = %s", appUpdateInfo.updateAvailability())
                }
            }
        })
    }

}