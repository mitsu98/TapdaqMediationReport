/*
 * Apps.kt
 * Copyright 2020 Axl Yody <axlyod@gmail.com>
 *
 */

package id.axlyody.tapdaqmediationreport.`interface`.request

import id.axlyody.tapdaqmediationreport.model.Apps
import retrofit2.Call
import retrofit2.http.GET

interface Apps {
    @GET("apps")
    fun result(): Call<List<Apps>>
}