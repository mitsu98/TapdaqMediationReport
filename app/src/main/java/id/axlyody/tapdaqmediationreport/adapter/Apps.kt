/*
 * Apps.kt
 * Copyright 2020 Axl Yody <axlyod@gmail.com>
 *
 */

package id.axlyody.tapdaqmediationreport.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import id.axlyody.tapdaqmediationreport.R
import id.axlyody.tapdaqmediationreport.databinding.AdapterAppsBinding
import id.axlyody.tapdaqmediationreport.model.Apps
import id.axlyody.tapdaqmediationreport.utils.load
import smartadapter.viewholder.SmartViewHolder

class AppsViewHolder(parentView: ViewGroup) : SmartViewHolder<Apps>(
    LayoutInflater.from(parentView.context).inflate(R.layout.adapter_apps, parentView, false)
) {


    override fun bind(item: Apps) {
        itemView.apply {
            AdapterAppsBinding.bind(this).apply {
                tvName.text = item.name
                tvPackageName.text = item._storeMeta.bundleId

                when (item.operatingSystem) {
                    "ios" -> ivOs.load(R.drawable.ic_apple)
                    "android" -> ivOs.load(R.drawable.ic_android)
                }
                item.sdk?.apply {
                    tvSdk.text =
                        String.format("SDK %s", "$versionMajor.$versionMinor.$versionPatch")
                }
            }


        }
    }

}