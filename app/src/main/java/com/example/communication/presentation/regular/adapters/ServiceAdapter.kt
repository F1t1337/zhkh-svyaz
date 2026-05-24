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
import com.example.communication.data.models.Service
import com.example.communication.data.models.ServiceStatus
import java.text.SimpleDateFormat
import java.util.Locale

class ServiceAdapter : ListAdapter<Service, ServiceAdapter.VH>(Diff) {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val viewAccent: View           = v.findViewById(R.id.view_service_accent)
        val tvType: TextView           = v.findViewById(R.id.tv_service_type_label)
        val tvStatus: TextView         = v.findViewById(R.id.tv_service_status)
        val tvDescription: TextView    = v.findViewById(R.id.tv_service_description)
        val tvApartment: TextView      = v.findViewById(R.id.tv_service_apartment)
        val tvDate: TextView           = v.findViewById(R.id.tv_service_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_service, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val s = getItem(position)
        val ctx = holder.itemView.context

        holder.tvType.text = s.serviceType

        // Description
        if (s.description.isNotBlank()) {
            holder.tvDescription.visibility = View.VISIBLE
            holder.tvDescription.text = s.description
        } else {
            holder.tvDescription.visibility = View.GONE
        }

        // Apartment
        holder.tvApartment.text = if (s.apartmentNumber.isNotBlank())
            "🏠 Квартира ${s.apartmentNumber}"
        else ctx.getString(R.string.service_hint_resident)

        // Date — show formatted
        holder.tvDate.text = try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val out = SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("ru"))
            out.format(sdf.parse(s.scheduledAt.take(10))!!)
        } catch (e: Exception) {
            s.scheduledAt.take(10)
        }

        // Status chip
        val (bgColorRes, textColorRes, labelRes) = when (s.status) {
            ServiceStatus.SCHEDULED   -> Triple(R.color.status_new_bg,      R.color.status_new_text,      R.string.service_status_scheduled)
            ServiceStatus.IN_PROGRESS -> Triple(R.color.status_progress_bg,  R.color.status_progress_text, R.string.service_status_progress)
            ServiceStatus.COMPLETED   -> Triple(R.color.status_done_bg,      R.color.status_done_text,     R.string.service_status_done)
            ServiceStatus.CANCELLED   -> Triple(R.color.status_rejected_bg,  R.color.status_rejected_text, R.string.service_status_cancelled)
        }
        holder.tvStatus.text = ctx.getString(labelRes)
        holder.tvStatus.setTextColor(ContextCompat.getColor(ctx, textColorRes))
        holder.tvStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(ctx, bgColorRes))

        // Accent bar colour by status
        holder.viewAccent.setBackgroundColor(ContextCompat.getColor(ctx, bgColorRes))
    }

    companion object Diff : DiffUtil.ItemCallback<Service>() {
        override fun areItemsTheSame(a: Service, b: Service) = a.id == b.id
        override fun areContentsTheSame(a: Service, b: Service) = a == b
    }
}
