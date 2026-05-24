package com.example.communication.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.communication.R
import com.example.communication.data.models.User
import com.example.communication.data.repositories.AuthRepositoryImpl
import com.example.communication.domain.usecases.auth.Login
import com.example.communication.presentation.regular.CoreActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(login = Login(AuthRepositoryImpl()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val tabRole = findViewById<TabLayout>(R.id.tab_role)
        val tilLogin = findViewById<TextInputLayout>(R.id.til_login)
        val etLogin = findViewById<TextInputEditText>(R.id.user_login)
        val tilPassword = findViewById<TextInputLayout>(R.id.til_password)
        val btnLogin = findViewById<MaterialButton>(R.id.button_input)
        val progress = findViewById<ProgressBar>(R.id.progress_bar)

        tabRole.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        tilLogin.hint = getString(R.string.hint_phone)
                        tilPassword.hint = getString(R.string.hint_passport)
                        etLogin.inputType = android.text.InputType.TYPE_CLASS_PHONE
                    }
                    1 -> {
                        tilLogin.hint = getString(R.string.hint_admin_login)
                        tilPassword.hint = getString(R.string.hint_password)
                        etLogin.inputType = android.text.InputType.TYPE_CLASS_TEXT
                    }
                }
                etLogin.text?.clear()
                findViewById<TextInputEditText>(R.id.user_password).text?.clear()
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        btnLogin.setOnClickListener {
            val identifier = etLogin.text?.toString()?.trim() ?: ""
            val password = findViewById<TextInputEditText>(R.id.user_password).text?.toString() ?: ""
            viewModel.login(identifier, password)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is AuthUiState.Idle -> {
                            progress.visibility = View.GONE
                            btnLogin.isEnabled = true
                        }
                        is AuthUiState.Loading -> {
                            progress.visibility = View.VISIBLE
                            btnLogin.isEnabled = false
                        }
                        is AuthUiState.Success -> {
                            progress.visibility = View.GONE
                            navigateToCore(state.user)
                        }
                        is AuthUiState.Error -> {
                            progress.visibility = View.GONE
                            btnLogin.isEnabled = true
                            Toast.makeText(this@AuthActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun navigateToCore(user: User) {
        val intent = Intent(this, CoreActivity::class.java).apply {
            when (user) {
                is User.regularUser -> {
                    putExtra(CoreActivity.EXTRA_IS_ADMIN, false)
                    putExtra(CoreActivity.EXTRA_USER_ID, user.id)
                    putExtra(CoreActivity.EXTRA_APARTMENT, user.apartmentNumber)
                    putExtra(CoreActivity.EXTRA_ENTRANCE, user.entrance)
                }
                is User.adminUser -> {
                    putExtra(CoreActivity.EXTRA_IS_ADMIN, true)
                    putExtra(CoreActivity.EXTRA_USER_ID, user.id)
                    putExtra(CoreActivity.EXTRA_APARTMENT, user.admLogin)
                    putExtra(CoreActivity.EXTRA_ENTRANCE, "")
                }
            }
        }
        startActivity(intent)
        finish()
    }
}
