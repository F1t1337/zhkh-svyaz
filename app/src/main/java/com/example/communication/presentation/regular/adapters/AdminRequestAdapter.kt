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
import com.google.android.material.button.MaterialButton

class AdminRequestAdapter(
    private val onChangeStatus: (Request) -> Unit
) : ListAdapter<Request, AdminRequestAdapter.VH>(Diff) {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val viewStatusAccent: View = v.findViewById(R.id.view_status_accent)
        val tvRequester: TextView = v.findViewById(R.id.tv_requester)
        val tvStatusBadge: TextView = v.findViewById(R.id.tv_status_badge)
        val tvCategory: TextView = v.findViewById(R.id.tv_category)
        val tvDescription: TextView = v.findViewById(R.id.tv_description)
        val tvCreated: TextView = v.findViewById(R.id.tv_created)
        val tvApartment: TextView = v.findViewById(R.id.tv_apartment)
        val btnChangeStatus: MaterialButton = v.findViewById(R.id.btn_change_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_admin_request, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val r = getItem(position)
        val ctx = holder.itemView.context

        holder.tvRequester.text = "Жилец #${r.residentId}"
        holder.tvApartment.text = "Кв. ${r.residentId}"
        holder.tvDescription.text = r.description
        holder.tvCreated.text = r.createdAt.take(10)

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
        holder.tvStatusBadge.text = ctx.getString(labelRes)
        holder.tvStatusBadge.setTextColor(ContextCompat.getColor(ctx, textColor))
        holder.tvStatusBadge.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(ctx, bgColor))
        holder.viewStatusAccent.setBackgroundColor(ContextCompat.getColor(ctx, bgColor))

        holder.btnChangeStatus.setOnClickListener { onChangeStatus(r) }
    }

    companion object Diff : DiffUtil.ItemCallback<Request>() {
        override fun areItemsTheSame(a: Request, b: Request) = a.id == b.id
        override fun areContentsTheSame(a: Request, b: Request) = a == b
    }
}
