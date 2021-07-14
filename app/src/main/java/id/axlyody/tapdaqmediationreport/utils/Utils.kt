/*
 * Utils.kt
 * Copyright 2020 Axl Yody <axlyod@gmail.com>
 *
 */

package id.axlyody.tapdaqmediationreport.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import id.axlyody.tapdaqmediationreport.database.Revenue
import id.axlyody.tapdaqmediationreport.model.Mediation
import id.axlyody.tapdaqmediationreport.repository.PreferencesRepository
import timber.log.Timber
import java.text.DateFormat
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ln
import kotlin.math.pow

fun Activity.start(clazz: Class<*>) {
    startActivity(Intent(this, clazz))
}

fun String.getDayFromDate(): String {
    val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat: DateFormat = SimpleDateFormat("E dd", Locale.getDefault())
    try {
        val date = inputFormat.parse(this)!!
        return outputFormat.format(date)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return "error"
}

fun Long.updateTime(pref: PreferencesRepository): String {
    pref.save("data_updated", this)
    return String.format("Data updated at %s", formatTimeMilis())
}

fun getUpdateTime(pref: PreferencesRepository): String {
    return if(pref.contains("data_updated")) {
        val time = pref.get("data_updated", 0.toLong()) as Long
        String.format("Last refreshed at %s", time.formatTimeMilis())
    } else {
        ""
    }
}

fun timestamps(): Long {
    return Calendar.getInstance().timeInMillis
}

fun Long.formatTimeMilis(): String {
    val time = Calendar.getInstance()
    time.timeInMillis = this
    val output = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
    return output.format(time.time)
}

fun getDate(set: Int? = null, add: Int? = null, value: Int? = null): String {
    val date = Calendar.getInstance()
    val output = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    set?.let {
        date.set(it, value!!)
    }
    add?.let {
        date.add(it, value!!)
    }
    return output.format(date.time)
}


fun getResColor(context: Context, colorId: Int): Int {
    var color: Int
    TypedValue().apply {
        context.obtainStyledAttributes(data, intArrayOf(colorId))
            .apply {
                color = getColor(0, 0)
                recycle()
            }
    }
    return color
}

fun ImageView.load(url: Any) {
    try {
        Glide.with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
    } catch (e: Exception) {
    }
}


enum class groupRevenue {
    DATE,
    AD_NETWORK,
    NO_GROUP
}

fun List<Mediation>.insertDaily(): List<Revenue> {
    return map {
        Revenue(
            ad_request_total = it.ad_request_total,
            ad_request_success = it.ad_request_success,
            fill_rate = it.fill_rate,
            revenue = it.revenue,
            ecpm = it.ecpm,
            impressions = it.impressions,
            country = "id",
            ad_network = it.group.ad_network,
            app = it.group.app,
            date = it.group.date,
            updatedAt = timestamps()
        )
    }
}

fun List<Revenue>.sumRevenue(
    range: ClosedRange<String>? = null,
    sort: groupRevenue? = null
): List<Revenue> {
    return if (find { it.date == getDate() } == null) {
        filter {
            range?.let { r ->
                it.date in r
            } ?: kotlin.run {
                it.date in getDate(add = Calendar.DATE, value = -1)..getDate()
            }
        }.sortedBy {
            it.date
        }.groupBy {
            when (sort) {
                groupRevenue.DATE -> it.date
                groupRevenue.AD_NETWORK -> it.ad_network
                groupRevenue.NO_GROUP -> null
                else -> it.date
            }
        }
            .values
            .map { item ->
                item.reduce { acc, mediation ->
                    Revenue(
                        ad_request_total = mediation.ad_request_total,
                        ad_request_success = mediation.ad_request_success,
                        fill_rate = mediation.fill_rate,
                        revenue = acc.revenue + mediation.revenue,
                        ecpm = acc.ecpm + mediation.ecpm,
                        impressions = acc.impressions + mediation.impressions,
                        country = "id",
                        ad_network = mediation.ad_network,
                        app = mediation.app,
                        date = mediation.date
                    )
                }
            }
    } else {
        filter {
            range?.let { r ->
                it.date in r
            } ?: kotlin.run {
                it.date in getDate()..getDate()

            }
        }.sortedBy {
            it.date
        }.groupBy {
            when (sort) {
                groupRevenue.DATE -> it.date
                groupRevenue.AD_NETWORK -> it.ad_network
                groupRevenue.NO_GROUP -> null
                else -> it.date
            }
        }
            .values
            .map { item ->
                item.reduce { acc, mediation ->
                    Revenue(
                        ad_request_total = mediation.ad_request_total,
                        ad_request_success = mediation.ad_request_success,
                        fill_rate = mediation.fill_rate,
                        revenue = acc.revenue + mediation.revenue,
                        ecpm = acc.ecpm + mediation.ecpm,
                        impressions = acc.impressions + mediation.impressions,
                        country = "id",
                        ad_network = mediation.ad_network,
                        app = mediation.app,
                        date = mediation.date
                    )
                }
            }
    }
}

fun Int.dateRange(to: String? = null): ClosedRange<String> {
    return getDate(
        add = Calendar.DATE,
        value = this
    )..(to ?: getDate())
}

fun Int.formatTotal(): String {
    return NumberFormat.getNumberInstance(Locale.GERMAN).format(this)
}

fun Long.withSuffix(): String {
    if (this < 1000) return "" + this
    val exp = (ln(this.toDouble()) / ln(100.0)).toInt()
    return String.format(
        "%.2f %c",
        this / 1000.0.pow(exp.toDouble()),
        "KMGTPE"[exp - 1]
    )
}

fun Context.toast(message: String) {
    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
}




