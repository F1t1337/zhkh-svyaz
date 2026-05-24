package com.example.communication.presentation.regular

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.communication.R
import com.example.communication.data.session.SessionManager
import com.example.communication.presentation.auth.AuthActivity
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class CoreActivity : AppCompatActivity() {

    private lateinit var fragments: Map<Int, Fragment>
    private var currentFragmentId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_core)

        val isAdmin = intent.getBooleanExtra(EXTRA_IS_ADMIN, false)
        val userId = intent.getStringExtra(EXTRA_USER_ID) ?: ""
        val apartment = intent.getStringExtra(EXTRA_APARTMENT) ?: ""
        val entrance = intent.getStringExtra(EXTRA_ENTRANCE) ?: ""

        setupToolbar(isAdmin, apartment, entrance)
        setupFragments(isAdmin, userId, apartment, entrance, savedInstanceState)
        setupBottomNav(isAdmin)
        setupLogout()
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

    companion object {
        const val EXTRA_IS_ADMIN = "extra_is_admin"
        const val EXTRA_USER_ID = "extra_user_id"
        const val EXTRA_APARTMENT = "extra_apartment"
        const val EXTRA_ENTRANCE = "extra_entrance"
    }
}
