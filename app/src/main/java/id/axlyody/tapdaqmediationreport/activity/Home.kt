/*
 * Home.kt
 * Copyright 2020 Axl Yody <axlyod@gmail.com>
 *
 */

package id.axlyody.tapdaqmediationreport.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import id.axlyody.tapdaqmediationreport.AppActivity
import id.axlyody.tapdaqmediationreport.R
import id.axlyody.tapdaqmediationreport.database.Revenue
import id.axlyody.tapdaqmediationreport.databinding.ActivityHomeBinding
import id.axlyody.tapdaqmediationreport.fragment.LayoutLoading
import id.axlyody.tapdaqmediationreport.service.AppUpdate
import id.axlyody.tapdaqmediationreport.utils.NavigationBottomBarSectionsStateKeeperWorkaround
import id.axlyody.tapdaqmediationreport.utils.getDate
import id.axlyody.tapdaqmediationreport.utils.start
import id.axlyody.tapdaqmediationreport.utils.timestamps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class Home : AppActivity() {
    lateinit var binding: ActivityHomeBinding

    private var dailyJob: MenuItem? = null
    lateinit var appUpdate: AppUpdate

    private suspend fun getData(jsonObject: JsonObject) = service.getRevenue(jsonObject)

    private val navSection by lazy {
        NavigationBottomBarSectionsStateKeeperWorkaround(
            activity = this,
            navHostContainerID = R.id.nav_host,
            navGraphIds = listOf(
                R.navigation.nav_overview,
                R.navigation.nav_apps,
                R.navigation.nav_mediation
            ),
            bottomNavigationViewID = R.id.home_bottom_navbutton
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)
        navSection.onCreate(savedInstanceState)
        fetchDataFirstTime()

        appUpdate = AppUpdate(this)
        appUpdate.update(binding.layout)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        appUpdate.activityResult(resultCode)
    }

    override fun onSupportNavigateUp() = navSection.onSupportNavigateUp()

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        navSection.onRestoreInstanceState()
    }

    override fun onBackPressed() {
        if (!navSection.onSupportNavigateUp())
            super.onBackPressed()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        dailyJob = menu?.findItem(R.id.menu_runDailyData)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_about -> {
                start(About::class.java)
            }
            R.id.menu_logout -> {
                pref.delete("login")
                revenueViewModel.deleteAll()
                start(Main::class.java)
                finish()
            }
            R.id.menu_settings -> start(Settings::class.java)
            R.id.menu_runDailyData -> {
                dailyJob?.setIcon(R.drawable.ic_c_start_green)

            }
            R.id.menu_fetchData -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun fetchDataFirstTime() {
        if (intent.hasExtra("fetchDataFirstTime")) {
            launch {
                val loading = LayoutLoading()
                loading.show(supportFragmentManager, "test")
                withContext(Dispatchers.IO) {
                    getData(
                        JsonObject().apply {
                            addProperty("start_time", getDate(add = Calendar.YEAR, value = -1))
                            addProperty("end_time", getDate())
                            add("group_by", JsonArray().apply {
                                add("app")
                                add("date")
                                add("ad_network")
                            })
                        }
                    )
                }.map {
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
                        date = it.group.date,
                        updatedAt = timestamps()
                    )
                }.also {
                    revenueViewModel.deleteAll()
                    revenueViewModel.insertRevenue(it)
                    loading.dialog?.dismiss()
                }
            }
        }
    }

}