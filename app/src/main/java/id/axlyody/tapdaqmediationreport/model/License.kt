/*
 * License.kt
 * Copyright 2020 Axl Yody <axlyod@gmail.com>
 *
 */

package id.axlyody.tapdaqmediationreport.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class License(
    @Expose
    @SerializedName("libraries")
    val libraries: List<LicenseLibraries>,
)

data class LicenseLibraries(
    @Expose
    @SerializedName("license")
    val license: String,
    @Expose
    @SerializedName("licenseUrl")
    val licenseUrl: String,
    @Expose
    @SerializedName("normalizedName")
    val normalizedName: String,
    @Expose
    @SerializedName("url")
    val url: String,
    @Expose
    @SerializedName("libraryName")
    val libraryName: String
)