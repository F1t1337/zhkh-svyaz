package com.example.communication.presentation.regular.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.communication.R
import com.example.communication.data.models.Notification
import com.example.communication.presentation.regular.ResidentViewModel
import com.example.communication.presentation.regular.ResidentViewModelFactory
import com.example.communication.presentation.regular.adapters.NotificationAdapter
import com.example.communication.presentation.utils.AnimUtils
import com.example.communication.presentation.utils.animateItems
import kotlinx.coroutines.launch

class NotificationsFragment : Fragment() {

    private val viewModel: ResidentViewModel by activityViewModels { ResidentViewModelFactory() }
    private lateinit var adapter: NotificationAdapter
    private lateinit var rv: RecyclerView
    private var apartment: String = ""
    private var firstLoad = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_notifications, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rv = view.findViewById(R.id.rv_notifications)
        val tvEmpty = view.findViewById<TextView>(R.id.tv_empty)
        val progress = view.findViewById<ProgressBar>(R.id.progress_bar)
        val swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)

        apartment = arguments?.getString(ARG_APARTMENT) ?: return

        adapter = NotificationAdapter(onItemClick = { notif -> showNotificationDetail(notif) })
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(requireContext())

        swipeRefresh.setColorSchemeResources(R.color.color_primary)
        swipeRefresh.setOnRefreshListener { viewModel.loadNotifications(apartment) }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.notifications.collect { list ->
                        adapter.submitList(list)
                        tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                        rv.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
                        if (list.isNotEmpty() && firstLoad) {
                            rv.animateItems()
                            firstLoad = false
                        }
                    }
                }
                launch {
                    viewModel.isLoading.collect { loading ->
                        if (!loading) swipeRefresh.isRefreshing = false
                        progress.visibility = if (loading && adapter.currentList.isEmpty()) View.VISIBLE else View.GONE
                    }
                }
            }
        }

        viewModel.loadNotifications(apartment)
    }

    private fun showNotificationDetail(notification: Notification) {
        if (!notification.isRead) {
            viewModel.markNotificationRead(notification.id, apartment)
        }
        AlertDialog.Builder(requireContext())
            .setTitle(notification.title)
            .setMessage(buildString {
                appendLine(notification.body)
                appendLine()
                append("Дата: ${notification.sentAt.take(10)}")
            })
            .setPositiveButton(R.string.close, null)
            .show()
    }

    companion object {
        const val ARG_APARTMENT = "arg_apartment"
        fun newInstance(apartment: String) = NotificationsFragment().apply {
            arguments = Bundle().apply { putString(ARG_APARTMENT, apartment) }
        }
    }
}
