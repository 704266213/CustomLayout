package com.customview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.customview.R


class SampleAdapter : RecyclerView.Adapter<SampleAdapter.SampleViewHold>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleViewHold {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.sample_item, parent, false)
        return SampleViewHold(itemView)
    }

    override fun getItemCount(): Int {
        return 100
    }

    override fun onBindViewHolder(holder: SampleViewHold, position: Int) {

    }


    class SampleViewHold(itemView: View) : RecyclerView.ViewHolder(itemView)
}