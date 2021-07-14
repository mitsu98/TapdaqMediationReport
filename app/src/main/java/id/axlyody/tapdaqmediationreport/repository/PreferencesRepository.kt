/*
 * PreferencesRepository.kt
 * Copyright 2020 Axl Yody <axlyod@gmail.com>
 *
 */

package id.axlyody.tapdaqmediationreport.repository

import android.app.Application
import android.content.Context

class PreferencesRepository(application: Application) {

    private val pref by lazy {
        application.run {
            getSharedPreferences(packageName, Context.MODE_PRIVATE)
        }
    }

    fun save(key: String, value: Any) {
        pref.edit().run {
            when (value) {
                is Boolean -> putBoolean(key, value)
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
            }
            apply()
        }
    }


    fun delete(key: String) {
        pref.edit().run {
            remove(key)
            apply()
        }
    }

    fun get(key: String, default: Any): Any {
        return pref.run {
            when (default) {
                is Boolean -> getBoolean(key, default)
                is String -> getString(key, default)!!
                is Int -> getInt(key, default)
                is Long -> getLong(key, default)
                else -> Any()
            }
        }
    }


    fun contains(key: String): Boolean {
        return pref.contains(key)
    }

}
