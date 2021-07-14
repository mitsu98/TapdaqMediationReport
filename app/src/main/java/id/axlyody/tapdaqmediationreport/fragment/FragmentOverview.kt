/*
 * FragmentOverview.kt
 * Copyright 2020 Axl Yody <axlyod@gmail.com>
 *
 */

package id.axlyody.tapdaqmediationreport.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.hadiidbouk.charts.BarData
import id.axlyody.tapdaqmediationreport.AppFragment
import id.axlyody.tapdaqmediationreport.R
import id.axlyody.tapdaqmediationreport.adapter.NetworksViewHolder
import id.axlyody.tapdaqmediationreport.databinding.FragmentOverviewBinding
import id.axlyody.tapdaqmediationreport.model.Mediation
import id.axlyody.tapdaqmediationreport.model.Networks
import id.axlyody.tapdaqmediationreport.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.HttpException
import smartadapter.SmartRecyclerAdapter
import java.util.*

class FragmentOverview : AppFragment() {
    lateinit var binding: FragmentOverviewBinding

    private var data: List<Mediation>? = null
    private var request: Call<List<Mediation>>? = null
    private suspend fun getRevenue(toJSONObject: JsonObject) = service.getRevenue(
        toJSONObject
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireView().apply {
            binding.refreshLayout.apply {
                setProgressBackgroundColorSchemeColor(
                    getResColor(
                        requireContext(),
                        R.attr.colorPrimary
                    )
                )
                setColorSchemeColors(Color.WHITE)
                setOnRefreshListener {
                    getData()
                }
            }

        }

        retrieveData()

    }

    override fun onPause() {
        super.onPause()
        request?.cancel()
    }


    private fun getData() {
        requireView().apply {
            launch {
                try {
                    data = withContext(Dispatchers.IO) {
                        getRevenue(
                            JsonObject().apply {
                                addProperty("start_time", getDate(add = Calendar.DATE, value = -1))
                                addProperty("end_time", getDate())
                                add("group_by", JsonArray().apply {
                                    add("app")
                                    add("date")
                                    add("ad_network")
                                })
                            }
                        )
                    }

                    data?.apply {
                        revenueViewModel.upsert(insertDaily())
                    }
                    binding.refreshLayout.isRefreshing = false


                } catch (throwable: HttpException) {
                    activity?.runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            throwable.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    binding.refreshLayout.isRefreshing = false

                }
            }

        }
    }

    private fun retrieveData() {
        binding.refreshLayout.isRefreshing = false
        revenueViewModel.getRangeRevenue(getDate(add = Calendar.YEAR, value = -1), getDate())
            ?.observe(requireActivity(), { res ->
                try {
                    binding.txTodaySummary.text = String.format(
                        "$%s",
                        (res.sumRevenue().last().revenue / 1000)
                            .toLong()
                            .withSuffix()
                    )

                    binding.txWeekSummary.text = String.format(
                        "$%s",
                        (res.sumRevenue(getDate(set = Calendar.DAY_OF_WEEK, value = 2)..getDate())
                            .sumOf { it.revenue / 1000 })
                            .toLong()
                            .withSuffix()
                    )

                    binding.txMonthSummary.text = String.format(
                        "$%s",
                        (res.sumRevenue(getDate(set = Calendar.DAY_OF_MONTH, value = 1)..getDate())
                            .sumOf { it.revenue / 1000 })
                            .toLong()
                            .withSuffix()
                    )


                    binding.txYesterdayRevenue.text =
                        String.format(
                            "$%s",
                            (res.sumRevenue(
                                (-1).dateRange(
                                    getDate(
                                        add = Calendar.DATE,
                                        value = -1
                                    )
                                )
                            ).last().revenue / 1000)
                                .toLong()
                                .withSuffix()
                        )
                    binding.txWeekRevenue.text =
                        String.format(
                            "$%s",
                            (res.sumRevenue((-7).dateRange())
                                .sumOf { it.revenue / 1000 })
                                .toLong()
                                .withSuffix()
                        )
                    binding.txMonthRevenue.text =
                        String.format(
                            "$%s",
                            (res.sumRevenue((-30).dateRange())
                                .sumOf { it.revenue } / 1000)
                                .toLong()
                                .withSuffix()
                        )

                    binding.txYestedayImpression.text =
                        String.format(
                            "%s",
                            res.sumRevenue((-1).dateRange())
                                .last()
                                .impressions
                                .formatTotal()
                        )
                    binding.txWeekImpression.text =
                        String.format(
                            "%s",
                            res.sumRevenue((-7).dateRange())
                                .sumOf { it.impressions }
                                .formatTotal()
                        )
                    binding.txMonthImpression.text =
                        String.format(
                            "%s",
                            res.sumRevenue((-30).dateRange())
                                .sumOf { it.impressions }
                                .formatTotal()
                        )

                    binding.txYesterdayCpm.text = String.format(
                        "$%.2f",
                        res.sumRevenue(
                            (-1).dateRange(getDate(add = Calendar.DATE, value = -1))
                        )
                            .last()
                            .ecpm / 1000
                    )
                    binding.txWeekCpm.text =
                        String.format(
                            "$%.2f",
                            res.sumRevenue((-7).dateRange())
                                .map { it.ecpm / 1000 }
                                .average()
                        )

                    binding.txMonthCpm.text =
                        String.format(
                            "$%.2f",
                            res.sumRevenue((-30).dateRange())
                                .map { it.ecpm / 1000 }
                                .average()
                        )


                    //CHART
                    res.sumRevenue((-7).dateRange()).also {
                        binding.cpDailyEcpm.apply {
                            setDataList(it.map {
                                BarData(
                                    String.format(
                                        "%s\n$%.2f",
                                        it.date.getDayFromDate(),
                                        it.ecpm / 1000
                                    ),
                                    (it.ecpm / 1000),
                                    ""
                                )
                            } as ArrayList<BarData>)
                            setMaxValue(it.maxOf { it.ecpm } / 1000)
                            build()
                        }
                    }.also {
                        binding.cpDailyRevenue.apply {
                            setDataList(it.map {
                                BarData(
                                    String.format(
                                        "%s\n$%d",
                                        it.date.getDayFromDate(),
                                        it.revenue / 1000
                                    ),
                                    (it.revenue / 1000).toFloat(),
                                    ""
                                )
                            } as ArrayList<BarData>)
                            setMaxValue((it.maxOf { it.revenue } / 1000).toFloat())
                            build()
                        }
                    }.also {
                        binding.cpDailyImpression.apply {
                            setDataList(it.map {
                                BarData(
                                    String.format(
                                        "%s\n%s",
                                        it.date.getDayFromDate(),
                                        it.impressions.toLong().withSuffix()
                                    ),
                                    (it.impressions).toFloat(),
                                    ""
                                )
                            } as ArrayList<BarData>)
                            setMaxValue((it.maxOf { it.impressions }).toFloat())
                            clearAnimation()
                            build()
                        }
                    }


                    //NETWORKS
                    SmartRecyclerAdapter.items(
                        res.sumRevenue((-360).dateRange(), groupRevenue.AD_NETWORK).map { r ->
                            Networks(
                                name = r.ad_network,
                                revenue = r.revenue / 1000
                            )
                        }
                    )
                        .setLayoutManager(
                            LinearLayoutManager(
                                requireContext(),
                                RecyclerView.HORIZONTAL,
                                false
                            )
                        )
                        .map(Networks::class, NetworksViewHolder::class)
                        .into<SmartRecyclerAdapter>(binding.rvNetworks)

                    res.last().updatedAt?.let {
                        binding.txUpdatedAt.text =
                            String.format("Last updated at %s", it.formatTimeMilis())
                    }

                    binding.contentLayout.visibility = View.VISIBLE

                } catch (e: Exception) {

                }
            })

        getData()
    }
}



