package com.example.communication.presentation.regular.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.communication.R
import com.example.communication.data.models.Notification
import com.example.communication.data.models.NotificationType

class NotificationAdapter(
    private val onItemClick: (Notification) -> Unit = {}
) : ListAdapter<Notification, NotificationAdapter.VH>(Diff) {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val ivTypeIcon: TextView = v.findViewById(R.id.iv_type_icon)
        val tvTitle: TextView = v.findViewById(R.id.tv_title)
        val dotUnread: View = v.findViewById(R.id.dot_unread)
        val tvBody: TextView = v.findViewById(R.id.tv_body)
        val tvDate: TextView = v.findViewById(R.id.tv_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val n = getItem(position)
        holder.tvTitle.text = n.title
        holder.tvBody.text = n.body
        holder.tvDate.text = n.sentAt.take(10)
        holder.dotUnread.visibility = if (n.isRead) View.GONE else View.VISIBLE
        holder.ivTypeIcon.text = when (n.type) {
            NotificationType.GENERAL -> "📢"
            NotificationType.REQUEST_UPDATE -> "📋"
            NotificationType.RECEIPT -> "🧾"
            NotificationType.EMERGENCY -> "🚨"
        }
        holder.itemView.setOnClickListener { onItemClick(n) }
    }

    companion object Diff : DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(a: Notification, b: Notification) = a.id == b.id
        override fun areContentsTheSame(a: Notification, b: Notification) = a == b
    }
}
