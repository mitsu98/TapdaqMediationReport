/*
 * Mediation.kt
 * Copyright 2020 Axl Yody <axlyod@gmail.com>
 *
 */

package id.axlyody.tapdaqmediationreport.`interface`.request

import com.google.gson.JsonObject
import id.axlyody.tapdaqmediationreport.model.Mediation
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface Mediation {
    @POST("reports/mediation")
    fun result(
        @Body toJSONObject: JsonObject
    ): Call<List<Mediation>>
}







