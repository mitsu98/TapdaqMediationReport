/*
 * RevenueDao.kt
 * Copyright 2020 Axl Yody <axlyod@gmail.com>
 *
 */

package id.axlyody.tapdaqmediationreport.database

import androidx.lifecycle.LiveData
import androidx.room.*
import id.axlyody.tapdaqmediationreport.utils.timestamps
import timber.log.Timber


@Dao
interface RevenueDao {
    @Query("SELECT * FROM revenue")
    suspend fun getAll(): List<Revenue>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(revenue: List<Revenue>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(revenue: Revenue)

    @Query("DELETE FROM revenue")
    suspend fun deleteAll()

    @Query("SELECT * FROM revenue WHERE ad_network = :ad_network AND date = :date LIMIT 1")
    suspend fun find(ad_network: String, date: String): Revenue?

    @Query("UPDATE revenue SET revenue=:revenue, impressions=:impressions, ecpm=:ecpm, fill_rate=:fill_rate, ad_request_success=:ad_request_success, ad_request_total=:ad_request_total, updatedAt=:updatedAt WHERE ad_network=:ad_network AND date=:date")
    suspend fun update(
        ad_network: String,
        date: String,
        revenue: Int,
        impressions: Int,
        ecpm: Float,
        fill_rate: Int,
        ad_request_success: Int,
        ad_request_total: Int,
        updatedAt: Long = timestamps()
    )

    @Query("SELECT * FROM revenue WHERE Date BETWEEN :date_start AND :date_end")
    fun getRange(date_start: String, date_end: String): LiveData<List<Revenue>>

    @Transaction
    suspend fun upsert(revenue: List<Revenue>) {
        revenue.map {
            if (find(it.ad_network, it.date) == null) {
                insert(it)
            } else {
                update(
                    it.ad_network,
                    it.date,
                    it.revenue,
                    it.impressions,
                    it.ecpm,
                    it.fill_rate,
                    it.ad_request_success,
                    it.ad_request_total
                )
            }
        }
    }
}
