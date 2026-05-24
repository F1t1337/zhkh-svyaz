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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.communication.R
import com.example.communication.data.models.Notification
import com.example.communication.presentation.regular.ResidentViewModel
import com.example.communication.presentation.regular.ResidentViewModelFactory
import com.example.communication.presentation.utils.AnimUtils
import com.example.communication.presentation.utils.applyPressScale
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private val viewModel: ResidentViewModel by activityViewModels { ResidentViewModelFactory() }
    private var cardsAnimated = false
    private var notifCount = 0
    private var requestCount = 0
    private var receiptCount = 0

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

        // Slide-down header
        AnimUtils.slideDownIn(view.findViewById(R.id.tv_greeting), 0)
        AnimUtils.slideDownIn(view.findViewById(R.id.tv_apartment), 60)
        AnimUtils.slideDownIn(view.findViewById(R.id.tv_date), 120)

        val nav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val cardNotifications = view.findViewById<MaterialCardView>(R.id.card_stat_notifications)
        val cardRequests = view.findViewById<MaterialCardView>(R.id.card_stat_requests)
        val cardReceipts = view.findViewById<MaterialCardView>(R.id.card_stat_receipts)
        val cardMessenger = view.findViewById<MaterialCardView>(R.id.card_messenger)

        // Card press scale animation
        cardNotifications.applyPressScale()
        cardRequests.applyPressScale()
        cardReceipts.applyPressScale()
        cardMessenger.applyPressScale()

        cardNotifications.setOnClickListener { nav.selectedItemId = R.id.nav_notifications }
        cardRequests.setOnClickListener { nav.selectedItemId = R.id.nav_requests }
        cardReceipts.setOnClickListener { nav.selectedItemId = R.id.nav_receipts }
        cardMessenger.setOnClickListener { openMessenger() }

        val swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        swipeRefresh.setColorSchemeResources(R.color.color_primary)
        swipeRefresh.setOnRefreshListener { viewModel.loadAll(residentId, apartment) }

        val tvCountNotifications = view.findViewById<TextView>(R.id.tv_count_notifications)
        val tvCountRequests = view.findViewById<TextView>(R.id.tv_count_requests)
        val tvCountReceipts = view.findViewById<TextView>(R.id.tv_count_receipts)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.notifications.collect { list ->
                        val count = list.count { !it.isRead }
                        if (count != notifCount) {
                            AnimUtils.countUp(tvCountNotifications, count, 600)
                            notifCount = count
                        }
                        showRecentNotifications(view, list.take(3))
                    }
                }
                launch {
                    viewModel.requests.collect { list ->
                        val count = list.size
                        if (count != requestCount) {
                            AnimUtils.countUp(tvCountRequests, count, 600)
                            requestCount = count
                        }
                    }
                }
                launch {
                    viewModel.receipts.collect { list ->
                        val count = list.count { !it.isRead }
                        if (count != receiptCount) {
                            AnimUtils.countUp(tvCountReceipts, count, 600)
                            receiptCount = count
                        }
                    }
                }
                launch {
                    viewModel.isLoading.collect { loading ->
                        if (!loading) {
                            swipeRefresh.isRefreshing = false
                            // Animate stat cards only once after first load
                            if (!cardsAnimated) {
                                AnimUtils.staggerEnter(
                                    listOf(cardNotifications, cardRequests, cardReceipts, cardMessenger),
                                    delayStep = 70L, baseDelay = 180L
                                )
                                cardsAnimated = true
                            }
                        }
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
        notifications.forEachIndexed { index, notif ->
            val item = inflater.inflate(R.layout.item_notification_mini, container, false)
            item.findViewById<TextView>(R.id.tv_mini_title).text = notif.title
            item.findViewById<TextView>(R.id.tv_mini_body).text = notif.body
            item.findViewById<TextView>(R.id.tv_mini_date).text = notif.sentAt.take(10)
            val dot = item.findViewById<View>(R.id.dot_unread_mini)
            dot.visibility = if (notif.isRead) View.GONE else View.VISIBLE
            container.addView(item)
            // Staggered entrance for mini items
            item.alpha = 0f
            item.translationX = 60f
            item.animate()
                .alpha(1f)
                .translationX(0f)
                .setStartDelay(index * 80L + 100L)
                .setDuration(300)
                .setInterpolator(android.view.animation.DecelerateInterpolator(2f))
                .start()
        }
    }

    private fun openMessenger() {
        val url = viewModel.messengerUrl.value
        if (url.isBlank()) {
            Toast.makeText(requireContext(), getString(R.string.messenger_no_link), Toast.LENGTH_SHORT).show()
            return
        }
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: Exception) {
            Toast.makeText(requireContext(), R.string.messenger_not_installed, Toast.LENGTH_SHORT).show()
        }
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
