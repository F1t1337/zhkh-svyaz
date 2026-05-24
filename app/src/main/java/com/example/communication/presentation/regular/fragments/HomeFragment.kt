package com.example.communication.presentation.regular.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.communication.R
import com.example.communication.data.models.Notification
import com.example.communication.presentation.regular.ResidentViewModel
import com.example.communication.presentation.regular.ResidentViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private val viewModel: ResidentViewModel by activityViewModels { ResidentViewModelFactory() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val apartment = arguments?.getString(ARG_APARTMENT) ?: ""
        val entrance = arguments?.getString(ARG_ENTRANCE) ?: ""
        val residentId = arguments?.getString(ARG_RESIDENT_ID) ?: ""

        val dateStr = SimpleDateFormat("d MMMM yyyy", Locale("ru")).format(Date())

        view.findViewById<TextView>(R.id.tv_greeting).text = "Добрый день!"
        view.findViewById<TextView>(R.id.tv_apartment).text =
            getString(R.string.home_subtitle, apartment, entrance)
        view.findViewById<TextView>(R.id.tv_date).text = dateStr

        val nav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)

        view.findViewById<MaterialCardView>(R.id.card_stat_notifications).setOnClickListener {
            nav.selectedItemId = R.id.nav_notifications
        }
        view.findViewById<MaterialCardView>(R.id.card_stat_requests).setOnClickListener {
            nav.selectedItemId = R.id.nav_requests
        }
        view.findViewById<MaterialCardView>(R.id.card_stat_receipts).setOnClickListener {
            nav.selectedItemId = R.id.nav_receipts
        }
        view.findViewById<MaterialCardView>(R.id.card_messenger).setOnClickListener {
            showMessengerDialog()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.notifications.collect { list ->
                        view.findViewById<TextView>(R.id.tv_count_notifications).text =
                            list.count { !it.isRead }.toString()
                        showRecentNotifications(view, list.take(3))
                    }
                }
                launch {
                    viewModel.requests.collect { list ->
                        view.findViewById<TextView>(R.id.tv_count_requests).text =
                            list.size.toString()
                    }
                }
                launch {
                    viewModel.receipts.collect { list ->
                        view.findViewById<TextView>(R.id.tv_count_receipts).text =
                            list.count { !it.isRead }.toString()
                    }
                }
            }
        }

        viewModel.loadAll(residentId, apartment)
    }

    private fun showRecentNotifications(root: View, notifications: List<Notification>) {
        val container = root.findViewById<LinearLayout>(R.id.container_recent)
        val tvNoRecent = root.findViewById<TextView>(R.id.tv_no_recent)
        container.removeAllViews()

        if (notifications.isEmpty()) {
            tvNoRecent.visibility = View.VISIBLE
            return
        }
        tvNoRecent.visibility = View.GONE

        val inflater = LayoutInflater.from(requireContext())
        notifications.forEach { notif ->
            val item = inflater.inflate(R.layout.item_notification_mini, container, false)
            item.findViewById<TextView>(R.id.tv_mini_title).text = notif.title
            item.findViewById<TextView>(R.id.tv_mini_body).text = notif.body
            item.findViewById<TextView>(R.id.tv_mini_date).text = notif.sentAt.take(10)
            val dot = item.findViewById<View>(R.id.dot_unread_mini)
            dot.visibility = if (notif.isRead) View.GONE else View.VISIBLE
            container.addView(item)
        }
    }

    private fun showMessengerDialog() {
        val ctx = requireContext()
        val options = arrayOf("Telegram", "ВКонтакте")
        AlertDialog.Builder(ctx)
            .setTitle("Выберите мессенджер")
            .setItems(options) { _, which ->
                val uri = if (which == 0) Uri.parse("tg://") else Uri.parse("vk://")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                if (intent.resolveActivity(ctx.packageManager) != null) startActivity(intent)
                else Toast.makeText(ctx, R.string.messenger_not_installed, Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    companion object {
        const val ARG_APARTMENT = "arg_apartment"
        const val ARG_ENTRANCE = "arg_entrance"
        const val ARG_RESIDENT_ID = "arg_resident_id"

        fun newInstance(apartment: String, entrance: String, residentId: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_APARTMENT, apartment)
                    putString(ARG_ENTRANCE, entrance)
                    putString(ARG_RESIDENT_ID, residentId)
                }
            }
    }
}
