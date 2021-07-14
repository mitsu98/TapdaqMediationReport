/*
 * Settings.kt
 * Copyright 2020 Axl Yody <axlyod@gmail.com>
 *
 */

package id.axlyody.tapdaqmediationreport.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.preference.PreferenceFragmentCompat
import id.axlyody.tapdaqmediationreport.AppActivity
import id.axlyody.tapdaqmediationreport.R

class Settings : AppActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        supportFragmentManager.beginTransaction().replace(
            R.id.settings_fragment, SettingsFragment()
        ).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }

        return true
    }
}

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }

}