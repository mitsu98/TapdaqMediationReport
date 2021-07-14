/*
 * Revenue.kt
 * Copyright 2020 Axl Yody <axlyod@gmail.com>
 *
 */

package id.axlyody.tapdaqmediationreport.database

import androidx.room.*
import java.util.*

@Entity(
    tableName = "revenue",
)
data class Revenue(
    @ColumnInfo(name = "revenue") var revenue: Int,
    @ColumnInfo(name = "impressions") var impressions: Int,
    @ColumnInfo(name = "ecpm") var ecpm: Float,
    @ColumnInfo(name = "fill_rate") var fill_rate: Int,
    @ColumnInfo(name = "ad_request_success") var ad_request_success: Int,
    @ColumnInfo(name = "ad_request_total") var ad_request_total: Int,
    @ColumnInfo(name = "app") var app: String,
    @ColumnInfo(name = "country") var country: String,
    @ColumnInfo(name = "ad_network") var ad_network: String,
    @ColumnInfo(name = "date") var date: String,
    @ColumnInfo(name = "updatedAt", defaultValue = "CURRENT_TIMESTAMP") var updatedAt: Long ? = null,


    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Long = 0,
)

