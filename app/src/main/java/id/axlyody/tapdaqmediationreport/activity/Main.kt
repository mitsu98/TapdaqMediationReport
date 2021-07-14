/*
 * Main.kt
 * Copyright 2020 Axl Yody <axlyod@gmail.com>
 *
 */

package id.axlyody.tapdaqmediationreport.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import id.axlyody.tapdaqmediationreport.AppActivity
import id.axlyody.tapdaqmediationreport.R
import id.axlyody.tapdaqmediationreport.databinding.ActivityMainBinding
import id.axlyody.tapdaqmediationreport.utils.start
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber

class Main : AppActivity() {
    lateinit var binding: ActivityMainBinding

    private suspend fun getAuth() = service.getApps()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        if (pref.contains("login")) {
            start(Home::class.java)
            finish()
        }
        binding.btSave.setOnClickListener {
            binding.txKey.apply {
                when {
                    text.isEmpty() -> {
                        binding.tbKey.setError(getString(R.string.main_error_field_blank), true)
                    }
                    text.length < 36 -> {
                        binding.tbKey.setError(
                            getString(R.string.main_error_token_must_36_chars),
                            true
                        )
                    }
                    else -> {
                        binding.tbKey.removeError()
                        login(binding.txKey.text.toString())
                    }
                }
            }
        }

    }

    private fun login(token: String) {
        binding.btSave.apply {
            isEnabled = false
            text = getString(R.string.main_wait)
        }

        launch {
            try {
                pref.save("login", token)
                withContext(Dispatchers.IO) {
                    getAuth()
                }.run {
                    Intent(this@Main, Home::class.java).run {
                        putExtra("fetchDataFirstTime", true)
                        startActivity(this)
                    }
                    finish()
                    Timber.d("Successfully login")
                }
            } catch (throwable: HttpException) {
                binding.btSave.apply {
                    isEnabled = true
                    text = getString(R.string.main_save)
                }

                runOnUiThread {
                    Toast.makeText(
                        this@Main,
                        when (throwable.code()) {
                            401 -> "Wrong API key"
                            else -> throwable.message()
                        },
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}

