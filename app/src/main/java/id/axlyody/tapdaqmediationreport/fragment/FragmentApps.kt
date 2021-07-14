/*
 * FragmentApps.kt
 * Copyright 2020 Axl Yody <axlyod@gmail.com>
 *
 */

package id.axlyody.tapdaqmediationreport.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import id.axlyody.tapdaqmediationreport.AppFragment
import id.axlyody.tapdaqmediationreport.adapter.AppsViewHolder
import id.axlyody.tapdaqmediationreport.databinding.FragmentAppsBinding
import id.axlyody.tapdaqmediationreport.model.Apps
import id.axlyody.tapdaqmediationreport.utils.gridAutoLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.HttpException
import smartadapter.SmartRecyclerAdapter


class FragmentApps : AppFragment() {
    lateinit var binding: FragmentAppsBinding

    private var data: List<Apps>? = null
    private var request: Call<List<Apps>>? = null
    private suspend fun getApps() = service.getApps()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        data?.let {
            retrieveData(it)
        } ?: run {
            getData()
        }
    }


    override fun onPause() {
        super.onPause()
        if (data == null) {
            request?.cancel()
        }
    }

    private fun getData() {
        requireView().apply {
            launch {
                try {
                    data = withContext(Dispatchers.IO) { getApps() }
                    data?.let {
                        retrieveData(it)
                    }
                } catch (throwable: HttpException) {
                    activity?.runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            throwable.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

        }
    }


    private fun retrieveData(data: List<Apps>) {
        binding.rvApps.apply {
            SmartRecyclerAdapter.items(data).map(Apps::class, AppsViewHolder::class)
                .setLayoutManager(gridAutoLayout(binding.rvApps, requireContext()))
                .into<SmartRecyclerAdapter>(this)
            visibility = View.VISIBLE
        }
    }

}