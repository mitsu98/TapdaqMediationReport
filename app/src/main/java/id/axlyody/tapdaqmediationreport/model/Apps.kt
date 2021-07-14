/*
 * Apps.kt
 * Copyright 2020 Axl Yody <axlyod@gmail.com>
 *
 */

package id.axlyody.tapdaqmediationreport.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Apps(
    @Expose
    @SerializedName("id")
    val id: String,
    @Expose
    @SerializedName("name")
    val name: String,
    @Expose
    @SerializedName("url")
    val url: String? = null,
    @Expose
    @SerializedName("iconUrl")
    val iconUrl: String? = null,
    @Expose
    @SerializedName("deeplinkUrl")
    val deeplinkUrl: String? = null,
    @Expose
    @SerializedName("sdk_mode")
    val sdk_mode: String,
    @Expose
    @SerializedName("operatingSystem")
    val operatingSystem: String,
    @Expose
    @SerializedName("_storeMeta")
    val _storeMeta: AppsMeta,
    @Expose
    @SerializedName("sdk")
    val sdk: AppsSDK? = null
)

data class AppsMeta(
    @Expose
    @SerializedName("id")
    val id: Int,
    @Expose
    @SerializedName("externalId")
    val externalId: String,
    @Expose
    @SerializedName("bundleId")
    val bundleId: String,
    @Expose
    @SerializedName("iconUrl")
    val iconUrl: String? = null,
    @Expose
    @SerializedName("type")
    val tye: String,
    @Expose
    @SerializedName("sync_status_success")
    val sync_status_success: Boolean
)

data class AppsSDK(
    @Expose
    @SerializedName("id")
    val id: Int,
    @Expose
    @SerializedName("versionMajor")
    val versionMajor: Int,
    @Expose
    @SerializedName("versionMinor")
    val versionMinor: Int,
    @Expose
    @SerializedName("versionPatch")
    val versionPatch: Int
)