/*
 * DailyRevenueScheduler.kt
 * Copyright 2020 Axl Yody <axlyod@gmail.com>
 *
 */

package id.axlyody.tapdaqmediationreport.service

import android.app.*
import android.app.Service
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import id.axlyody.tapdaqmediationreport.database.Revenue
import id.axlyody.tapdaqmediationreport.repository.PreferencesRepository
import id.axlyody.tapdaqmediationreport.repository.RevenueRepository

import id.axlyody.tapdaqmediationreport.utils.getDate
import id.axlyody.tapdaqmediationreport.utils.toast
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.coroutines.CoroutineContext


class DailyRevenueService : Service(), CoroutineScope {
    private val job by lazy { Job() }
    private val pref by lazy {
        PreferencesRepository(this.application)
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    lateinit var notification: Notification

    override fun onCreate() {
        if (Build.VERSION.SDK_INT >= 26) {
            val CHANNEL_ID = "my_channel_01"
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                channel
            )
            notification = Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Process")
                .setContentText("Foreground Process").build()
            startForeground(1, notification)
        }
    }

    private fun revenue() = RevenueRepository(this.application)


    private suspend fun getData(jsonObject: JsonObject) =
        Api(pref).getRevenue(jsonObject)

    override fun onBind(p0: Intent?): IBinder? {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        toast("Service started")
        Timber.i("SERVICE RUN")
        launch {
            withContext(Dispatchers.IO) {
                getData(
                    JsonObject().apply {
                        addProperty("start_time", getDate())
                        addProperty("end_time", getDate())
                        add("group_by", JsonArray().apply {
                            add("app")
                            add("date")
                            add("ad_network")
                        })
                    }
                ).map {
                    Timber.e(it.toString())
                    Revenue(
                        revenue = it.revenue,
                        impressions = it.impressions,
                        ecpm = it.ecpm,
                        fill_rate = it.fill_rate,
                        ad_request_success = it.ad_request_success,
                        ad_request_total = it.ad_request_total,
                        app = it.group.app,
                        country = "id",
                        ad_network = it.group.ad_network,
                        date = it.group.date
                    )
                }.also {
                    revenue().insert(it)
                }
            }
        }

        return START_NOT_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
        toast("Service ended")
        job.cancel()
    }

}

class DailyRevenueScheduler : JobService() {


    override fun onStartJob(p0: JobParameters?): Boolean {
        startService(Intent(this, DailyRevenueService::class.java))
        return false

    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        stopService(Intent(this, DailyRevenueService::class.java))
        return false
    }

}


fun DailySchedulerStart(application: Application) {

    val jobScheduler = application.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
    val jobInfo =
        JobInfo.Builder(123, ComponentName(application, DailyRevenueScheduler::class.java))
    val job = jobInfo.setRequiresCharging(false)
        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
        .setPeriodic(900000)
        .build()
    jobScheduler.schedule(job)
}

