/*
 * License.kt
 * Copyright 2020 Axl Yody <axlyod@gmail.com>
 *
 */

package id.axlyody.tapdaqmediationreport.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import id.axlyody.tapdaqmediationreport.R
import id.axlyody.tapdaqmediationreport.databinding.AdapterLicenseBinding
import id.axlyody.tapdaqmediationreport.model.LicenseLibraries
import smartadapter.viewholder.SmartViewHolder

class LicenseViewHolder(parentView: ViewGroup) : SmartViewHolder<LicenseLibraries>(
    LayoutInflater.from(parentView.context)
        .inflate(R.layout.adapter_license, parentView, false)
) {
    override fun bind(item: LicenseLibraries) {
        itemView.apply {
            AdapterLicenseBinding.bind(this).apply {
                tvName.text = item.libraryName
                tvLicense.text = item.license
            }


//            try {
//                setOnClickListener {
//                    Intent(Intent.ACTION_VIEW).apply {
//                        data = Uri.parse(item.url)
//                        it.context.startActivity(this)
//                    }
//                }
//            } catch (e: Exception) {
//
//            }
        }
    }

}