/*
 * Http.kt
 * Copyright 2020 Axl Yody <axlyod@gmail.com>
 *
 */

package id.axlyody.tapdaqmediationreport.service

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import id.axlyody.tapdaqmediationreport.model.Apps
import id.axlyody.tapdaqmediationreport.model.Mediation
import id.axlyody.tapdaqmediationreport.repository.PreferencesRepository
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.concurrent.TimeUnit


interface Service {
    @GET("apps")
    suspend fun getApps(): List<Apps>

    @POST("reports/mediation")
    suspend fun getRevenue(
        @Body toJSONObject: JsonObject
    ): List<Mediation>
}

fun Api(pref: PreferencesRepository): Service {
    return Retrofit.Builder()
        .baseUrl("https://analytics.tapdaq.com/v1/")
        .client(okhttp(pref))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler((Schedulers.io())))
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder().setLenient().create()
            )
        )
        .build()
        .create(Service::class.java)
}

private fun okhttp(pref: PreferencesRepository): OkHttpClient {
    return OkHttpClient()
        .newBuilder()
        .addInterceptor { chain ->
            chain.proceed(
                chain.request()
                    .newBuilder()
                    .apply {
                        addHeader(
                            "Authorization",
                            "Bearer ${pref.get("login", "0")}"
                        )
                        addHeader(
                            "User-Agent",
                            "TapdaqMediationReportApp/1.0"
                        )
                    }
                    .build()
            )
        }
        .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .connectTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .followRedirects(false)
        .build()
}