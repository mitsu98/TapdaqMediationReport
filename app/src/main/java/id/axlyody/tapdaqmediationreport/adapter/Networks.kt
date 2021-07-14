/*
 * Networks.kt
 * Copyright 2020 Axl Yody <axlyod@gmail.com>
 *
 */

package id.axlyody.tapdaqmediationreport.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import id.axlyody.tapdaqmediationreport.R
import id.axlyody.tapdaqmediationreport.databinding.AdapterNetworkBinding
import id.axlyody.tapdaqmediationreport.model.Networks
import smartadapter.viewholder.SmartViewHolder

class NetworksViewHolder(parentView: ViewGroup) : SmartViewHolder<Networks>(
    LayoutInflater.from(parentView.context)
        .inflate(R.layout.adapter_network, parentView, false)
) {
    override fun bind(item: Networks) {
        itemView.apply {
            AdapterNetworkBinding.bind(this).apply {
                tvName.text = String.format("%s", item.name)
                tvRevenue.text = String.format("$%d", item.revenue)
            }
        }
    }

}