/*
 * RevenueRepository.kt
 * Copyright 2020 Axl Yody <axlyod@gmail.com>
 *
 */

package id.axlyody.tapdaqmediationreport.repository

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import id.axlyody.tapdaqmediationreport.Resource
import id.axlyody.tapdaqmediationreport.database.AppDatabase
import id.axlyody.tapdaqmediationreport.database.Revenue
import id.axlyody.tapdaqmediationreport.database.RevenueDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class RevenueRepository(application: Application) {
    private val revenueDao: RevenueDao?

    init {
        val db = AppDatabase.getInstance(application.applicationContext)
        revenueDao = db?.revenue()
    }

    suspend fun getAll() = revenueDao?.getAll()


    fun getRange(date_start: String, date_end: String): LiveData<List<Revenue>>? {
        return revenueDao?.getRange(date_start, date_end)
    }

    fun insert(revenue: List<Revenue>) = runBlocking {
        launch(Dispatchers.IO) {
            revenueDao?.insertAll(revenue)
        }
    }

    fun deleteAll() = runBlocking {
        launch(Dispatchers.IO) {
            revenueDao?.deleteAll()
        }
    }

    fun upsert(revenue: List<Revenue>) = runBlocking {
        launch(Dispatchers.IO) {
            revenueDao?.upsert(revenue)
        }
    }
}

class RevenueViewModel(application: Application) : AndroidViewModel(application) {
    private var revenueRepository = RevenueRepository(application)

    fun insertRevenue(revenue: List<Revenue>) {
        revenueRepository.insert(revenue)
    }

    fun getAllRevenue() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = revenueRepository.getAll()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun getRangeRevenue(date_start: String, date_end: String): LiveData<List<Revenue>>? {
        return revenueRepository.getRange(date_start, date_end)
    }

    fun deleteAll() {
        revenueRepository.deleteAll()
    }

    fun upsert(revenue: List<Revenue>) {
        revenueRepository.upsert(revenue)
    }

}