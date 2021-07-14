/*
 * AppFragment.kt
 * Copyright 2020 Axl Yody <axlyod@gmail.com>
 *
 */

package id.axlyody.tapdaqmediationreport

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import id.axlyody.tapdaqmediationreport.repository.PreferencesRepository
import id.axlyody.tapdaqmediationreport.repository.RevenueViewModel
import id.axlyody.tapdaqmediationreport.service.Api
import id.axlyody.tapdaqmediationreport.service.Service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

open class AppFragment : Fragment(), CoroutineScope {
    lateinit var service: Service
    lateinit var job: Job

    //vm
    lateinit var pref: PreferencesRepository
    lateinit var revenueViewModel: RevenueViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        revenueViewModel = ViewModelProvider(this).get(RevenueViewModel::class.java)

        activity?.apply {
            pref = PreferencesRepository(application)
            job = Job()
            service = Api(pref)
        }
    }

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
}