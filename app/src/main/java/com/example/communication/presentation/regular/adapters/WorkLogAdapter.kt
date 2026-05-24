package com.example.communication.presentation.regular.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.communication.R
import com.example.communication.data.models.WorkLogEntry
import com.google.android.material.button.MaterialButton

class WorkLogAdapter : ListAdapter<WorkLogEntry, WorkLogAdapter.VH>(Diff) {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvWorkType: TextView = v.findViewById(R.id.tv_work_type)
        val tvDescription: TextView = v.findViewById(R.id.tv_description)
        val tvLocation: TextView = v.findViewById(R.id.tv_location)
        val tvDate: TextView = v.findViewById(R.id.tv_date)
        val btnPdf: MaterialButton = v.findViewById(R.id.btn_pdf)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_work_log, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val e = getItem(position)
        holder.tvWorkType.text = e.workType
        holder.tvDescription.text = e.description
        holder.tvLocation.text = e.location
        holder.tvDate.text = e.performedAt.take(10)
        holder.btnPdf.visibility = if (e.reportPdfUrl.isNotBlank()) View.VISIBLE else View.GONE
    }

    companion object Diff : DiffUtil.ItemCallback<WorkLogEntry>() {
        override fun areItemsTheSame(a: WorkLogEntry, b: WorkLogEntry) = a.id == b.id
        override fun areContentsTheSame(a: WorkLogEntry, b: WorkLogEntry) = a == b
    }
}
