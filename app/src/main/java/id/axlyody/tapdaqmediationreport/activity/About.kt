/*
 * About.kt
 * Copyright 2020 Axl Yody <axlyod@gmail.com>
 *
 */

package id.axlyody.tapdaqmediationreport.activity

import android.os.Bundle
import android.view.MenuItem
import id.axlyody.tapdaqmediationreport.AppActivity
import id.axlyody.tapdaqmediationreport.databinding.ActivityAboutBinding
import id.axlyody.tapdaqmediationreport.utils.start

class About : AppActivity() {
    lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)

        setContentView(binding.root)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        binding.btLicense.setOnClickListener {
            start(License::class.java)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

}