package com.example.communication.presentation.regular

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.communication.R
import com.example.communication.data.repositories.AuthRepositoryImpl
import com.example.communication.domain.usecases.auth.Logout
import com.example.communication.presentation.auth.AuthActivity
import kotlinx.coroutines.launch


class CoreActivity : AppCompatActivity() {

    private val viewModel: CoreViewModel by viewModels {
        CoreViewModelFactory(logout = Logout(AuthRepositoryImpl()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_core)

        val avatar: ImageButton = findViewById(R.id.imageButton)

        avatar.setOnClickListener { view ->
            val popupMenu = PopupMenu(this, view)
            popupMenu.menuInflater.inflate(R.menu.dropdown_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.Notifications_button -> {
                        startActivity(Intent(this, Notifications::class.java))
                        true
                    }
                    R.id.Settings_button -> {
                        startActivity(Intent(this, Settings::class.java))
                        true
                    }
                    R.id.Log_button -> {
                        // Теперь кнопка Log — это выход из аккаунта
                        viewModel.logout()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        collectUiState()
    }

    private fun collectUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state is CoreUiState.LoggedOut) {
                        val intent = Intent(this@CoreActivity, AuthActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }
}
