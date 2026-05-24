package com.example.communication.presentation.regular

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.communication.R
import com.example.communication.data.models.RequestStatus
import com.example.communication.data.session.SessionManager
import com.example.communication.presentation.auth.AuthActivity
import com.example.communication.presentation.regular.fragments.AdminHomeFragment
import com.example.communication.presentation.regular.fragments.AdminRequestsFragment
import com.example.communication.presentation.regular.fragments.AdminWorkLogFragment
import com.example.communication.presentation.regular.fragments.HomeFragment
import com.example.communication.presentation.regular.fragments.NotificationsFragment
import com.example.communication.presentation.regular.fragments.ReceiptsFragment
import com.example.communication.presentation.regular.fragments.RequestsFragment
import com.example.communication.presentation.regular.fragments.SendNotificationFragment
import com.example.communication.presentation.regular.fragments.ServicesFragment
import com.example.communication.presentation.regular.fragments.WorkLogFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class CoreActivity : AppCompatActivity() {

    private lateinit var fragments: Map<Int, Fragment>
    private var currentFragmentId = -1
    private var isAdmin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_core)

        isAdmin = intent.getBooleanExtra(EXTRA_IS_ADMIN, false)
        val userId = intent.getStringExtra(EXTRA_USER_ID) ?: ""
        val apartment = intent.getStringExtra(EXTRA_APARTMENT) ?: ""
        val entrance = intent.getStringExtra(EXTRA_ENTRANCE) ?: ""

        setupToolbar(isAdmin, apartment, entrance)
        setupFragments(isAdmin, userId, apartment, entrance, savedInstanceState)
        setupBottomNav(isAdmin)
        setupLogout()
        if (isAdmin) setupAdminBadges() else setupResidentBadges()
        if (!isAdmin) setupPasswordChange(userId)
    }

    private fun setupToolbar(isAdmin: Boolean, apartment: String, entrance: String) {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val tvName = toolbar.findViewById<TextView>(R.id.tv_user_name)
        val tvRole = toolbar.findViewById<TextView>(R.id.tv_user_role)

        if (isAdmin) {
            tvName.text = apartment
            tvRole.text = "Администратор"
        } else {
            tvName.text = "Квартира $apartment"
            tvRole.text = if (entrance.isNotBlank()) "Подъезд $entrance" else "Жилец"
        }
    }

    private fun setupFragments(isAdmin: Boolean, userId: String, apartment: String, entrance: String, savedInstanceState: Bundle?) {
        fragments = if (isAdmin) {
            mapOf(
                R.id.nav_admin_home to AdminHomeFragment.newInstance(),
                R.id.nav_admin_requests to AdminRequestsFragment.newInstance(),
                R.id.nav_send_notification to SendNotificationFragment.newInstance(userId),
                R.id.nav_admin_log to AdminWorkLogFragment.newInstance(userId),
                R.id.nav_services to ServicesFragment.newInstance()
            )
        } else {
            mapOf(
                R.id.nav_home to HomeFragment.newInstance(apartment, entrance, userId),
                R.id.nav_receipts to ReceiptsFragment.newInstance(userId),
                R.id.nav_requests to RequestsFragment.newInstance(userId),
                R.id.nav_notifications to NotificationsFragment.newInstance(apartment),
                R.id.nav_work_log to WorkLogFragment.newInstance()
            )
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().apply {
                fragments.values.forEach { add(R.id.fragment_container, it) }
                fragments.values.drop(1).forEach { hide(it) }
            }.commit()
            currentFragmentId = fragments.keys.first()
        }
    }

    private fun setupBottomNav(isAdmin: Boolean) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        if (isAdmin) {
            bottomNav.menu.clear()
            bottomNav.inflateMenu(R.menu.bottom_nav_admin)
        }

        bottomNav.setOnItemSelectedListener { item ->
            showFragment(item.itemId)
            true
        }
    }

    fun showFragment(navItemId: Int) {
        if (navItemId == currentFragmentId) return
        val target = fragments[navItemId] ?: return
        val current = fragments[currentFragmentId]

        supportFragmentManager.beginTransaction().apply {
            current?.let { hide(it) }
            show(target)
        }.commit()
        currentFragmentId = navItemId
    }

    private fun setupLogout() {
        findViewById<android.widget.ImageButton>(R.id.btn_logout).setOnClickListener {
            SessionManager.clear(this)
            startActivity(Intent(this, AuthActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }
    }

    private fun setupResidentBadges() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val vm = ViewModelProvider(this, ResidentViewModelFactory())[ResidentViewModel::class.java]

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    vm.notifications.collect { list ->
                        val unread = list.count { !it.isRead }
                        val badge = bottomNav.getOrCreateBadge(R.id.nav_notifications)
                        if (unread > 0) { badge.isVisible = true; badge.number = unread }
                        else bottomNav.removeBadge(R.id.nav_notifications)
                    }
                }
                launch {
                    vm.receipts.collect { list ->
                        val unread = list.count { !it.isRead }
                        val badge = bottomNav.getOrCreateBadge(R.id.nav_receipts)
                        if (unread > 0) { badge.isVisible = true; badge.number = unread }
                        else bottomNav.removeBadge(R.id.nav_receipts)
                    }
                }
                launch {
                    vm.requests.collect { list ->
                        val newCount = list.count { it.status == RequestStatus.NEW }
                        val badge = bottomNav.getOrCreateBadge(R.id.nav_requests)
                        if (newCount > 0) { badge.isVisible = true; badge.number = newCount }
                        else bottomNav.removeBadge(R.id.nav_requests)
                    }
                }
            }
        }
    }

    private fun setupAdminBadges() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val vm = ViewModelProvider(this, AdminViewModelFactory())[AdminViewModel::class.java]

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    vm.requests.collect { list ->
                        val newCount = list.count { it.status == RequestStatus.NEW }
                        val badge = bottomNav.getOrCreateBadge(R.id.nav_admin_requests)
                        if (newCount > 0) { badge.isVisible = true; badge.number = newCount }
                        else bottomNav.removeBadge(R.id.nav_admin_requests)
                    }
                }
            }
        }
    }

    private fun setupPasswordChange(userId: String) {
        // Password change is triggered from AuthActivity — no action needed here
        // It's accessible via the login screen only (before auth)
    }

    companion object {
        const val EXTRA_IS_ADMIN = "extra_is_admin"
        const val EXTRA_USER_ID = "extra_user_id"
        const val EXTRA_APARTMENT = "extra_apartment"
        const val EXTRA_ENTRANCE = "extra_entrance"
    }
}
