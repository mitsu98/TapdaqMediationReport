/*
 * AppActivity.kt
 * Copyright 2020 Axl Yody <axlyod@gmail.com>
 *
 */

package id.axlyody.tapdaqmediationreport

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import id.axlyody.tapdaqmediationreport.repository.PreferencesRepository
import id.axlyody.tapdaqmediationreport.repository.RevenueViewModel
import id.axlyody.tapdaqmediationreport.service.Api
import id.axlyody.tapdaqmediationreport.service.Service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

open class AppActivity : AppCompatActivity(), CoroutineScope {
    lateinit var service: Service
    lateinit var job: Job

    //vm
    lateinit var revenueViewModel: RevenueViewModel
    lateinit var pref: PreferencesRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        revenueViewModel = ViewModelProvider(this).get(RevenueViewModel::class.java)

        pref = PreferencesRepository(application)
        job = Job()
        service = Api(pref)

    }

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
}