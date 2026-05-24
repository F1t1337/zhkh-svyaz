package com.example.communication.presentation.regular.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.communication.R
import com.example.communication.data.models.Receipt

class ReceiptAdapter : ListAdapter<Receipt, ReceiptAdapter.VH>(Diff) {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvPeriod: TextView = v.findViewById(R.id.tv_period)
        val tvColdWater: TextView = v.findViewById(R.id.tv_cold_water)
        val tvHotWater: TextView = v.findViewById(R.id.tv_hot_water)
        val tvElectricity: TextView = v.findViewById(R.id.tv_electricity)
        val tvGas: TextView = v.findViewById(R.id.tv_gas)
        val tvTotal: TextView = v.findViewById(R.id.tv_total)
        val chipUnread: TextView = v.findViewById(R.id.chip_unread)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_receipt, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val r = getItem(position)
        holder.tvPeriod.text = r.period
        holder.tvColdWater.text = "%.2f ₽".format(r.coldWater)
        holder.tvHotWater.text = "%.2f ₽".format(r.hotWater)
        holder.tvElectricity.text = "%.2f ₽".format(r.electricity)
        holder.tvGas.text = "%.2f ₽".format(r.gas)
        holder.tvTotal.text = "%.2f ₽".format(r.totalAmount)
        holder.chipUnread.visibility = if (r.isRead) View.GONE else View.VISIBLE
    }

    companion object Diff : DiffUtil.ItemCallback<Receipt>() {
        override fun areItemsTheSame(a: Receipt, b: Receipt) = a.id == b.id
        override fun areContentsTheSame(a: Receipt, b: Receipt) = a == b
    }
}
