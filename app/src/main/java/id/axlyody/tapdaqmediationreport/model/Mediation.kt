/*
 * Mediation.kt
 * Copyright 2020 Axl Yody <axlyod@gmail.com>
 *
 */

package id.axlyody.tapdaqmediationreport.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Mediation(
    @Expose
    @SerializedName("id")
    val ad_request_total: Int,
    @Expose
    @SerializedName("fill_rate")
    val fill_rate: Int,
    @Expose
    @SerializedName("revenue")
    val revenue: Int,
    @Expose
    @SerializedName("ecpm")
    val ecpm: Float,
    @Expose
    @SerializedName("ad_request_success")
    val ad_request_success: Int,
    @Expose
    @SerializedName("impressions")
    val impressions: Int,
    @Expose
    @SerializedName("group")
    val group: MediationGroup,
)

data class MediationGroup(
    @Expose
    @SerializedName("app")
    val app: String,
    @Expose
    @SerializedName("date")
    val date: String,
    @Expose
    @SerializedName("ad_network")
    val ad_network: String
)

