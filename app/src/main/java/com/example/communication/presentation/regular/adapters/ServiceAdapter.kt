package com.example.communication.presentation.regular.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.communication.R
import com.example.communication.data.models.Service
import com.example.communication.data.models.ServiceStatus

class ServiceAdapter : ListAdapter<Service, ServiceAdapter.VH>(Diff) {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvServiceName: TextView = v.findViewById(R.id.tv_service_name)
        val tvServiceType: TextView = v.findViewById(R.id.tv_service_type)
        val tvContact: TextView = v.findViewById(R.id.tv_contact)
        val tvPhone: TextView = v.findViewById(R.id.tv_phone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_service, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val s = getItem(position)
        val ctx = holder.itemView.context

        holder.tvServiceName.text = s.title
        holder.tvServiceType.text = when (s.status) {
            ServiceStatus.SCHEDULED -> ctx.getString(R.string.service_status_scheduled)
            ServiceStatus.IN_PROGRESS -> ctx.getString(R.string.service_status_progress)
            ServiceStatus.COMPLETED -> ctx.getString(R.string.service_status_done)
            ServiceStatus.CANCELLED -> ctx.getString(R.string.service_status_cancelled)
        }
        holder.tvContact.text = "Жилец #${s.residentId}"
        holder.tvPhone.text = s.scheduledAt.take(10)
    }

    companion object Diff : DiffUtil.ItemCallback<Service>() {
        override fun areItemsTheSame(a: Service, b: Service) = a.id == b.id
        override fun areContentsTheSame(a: Service, b: Service) = a == b
    }
}
