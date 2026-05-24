package com.example.communication.presentation.regular.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.communication.R
import com.example.communication.data.models.Request
import com.example.communication.data.models.RequestCategory
import com.example.communication.data.models.RequestStatus

class RequestAdapter : ListAdapter<Request, RequestAdapter.VH>(Diff) {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val statusBar: View = v.findViewById(R.id.status_bar)
        val tvCategory: TextView = v.findViewById(R.id.tv_category)
        val chipStatus: TextView = v.findViewById(R.id.chip_status)
        val tvDescription: TextView = v.findViewById(R.id.tv_description)
        val tvCreated: TextView = v.findViewById(R.id.tv_created)
        val tvDeadline: TextView = v.findViewById(R.id.tv_deadline)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_request, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val r = getItem(position)
        val ctx = holder.itemView.context

        holder.tvDescription.text = r.description
        holder.tvCreated.text = ctx.getString(R.string.request_created, r.createdAt.take(10))
        holder.tvDeadline.text = ctx.getString(R.string.request_deadline, r.deadline.take(10))

        holder.tvCategory.text = when (r.category) {
            RequestCategory.PLUMBING -> ctx.getString(R.string.request_category_plumbing)
            RequestCategory.ELECTRICITY -> ctx.getString(R.string.request_category_electricity)
            RequestCategory.CLEANING -> ctx.getString(R.string.request_category_cleaning)
            RequestCategory.REPAIR -> ctx.getString(R.string.request_category_repair)
            RequestCategory.OTHER -> ctx.getString(R.string.request_category_other)
        }

        val (bgColor, textColor, labelRes) = when (r.status) {
            RequestStatus.NEW -> Triple(R.color.status_new_bg, R.color.status_new_text, R.string.request_status_new)
            RequestStatus.IN_PROGRESS -> Triple(R.color.status_progress_bg, R.color.status_progress_text, R.string.request_status_progress)
            RequestStatus.DONE -> Triple(R.color.status_done_bg, R.color.status_done_text, R.string.request_status_done)
            RequestStatus.REJECTED -> Triple(R.color.status_rejected_bg, R.color.status_rejected_text, R.string.request_status_rejected)
        }
        holder.chipStatus.text = ctx.getString(labelRes)
        holder.chipStatus.setTextColor(ContextCompat.getColor(ctx, textColor))
        holder.chipStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(ctx, bgColor))
        holder.statusBar.setBackgroundColor(ContextCompat.getColor(ctx, bgColor))
    }

    companion object Diff : DiffUtil.ItemCallback<Request>() {
        override fun areItemsTheSame(a: Request, b: Request) = a.id == b.id
        override fun areContentsTheSame(a: Request, b: Request) = a == b
    }
}
