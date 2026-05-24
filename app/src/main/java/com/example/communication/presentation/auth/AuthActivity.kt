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
import com.example.communication.data.repositories.supabase.SupabaseAuthRepository
import com.example.communication.data.session.SessionManager
import com.example.communication.domain.usecases.auth.Login
import com.example.communication.presentation.regular.CoreActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(login = Login(SupabaseAuthRepository()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Если сессия сохранена — пропускаем экран входа
        SessionManager.get(this)?.let { session ->
            navigateToCore(session.isAdmin, session.userId, session.apartment, session.entrance)
            return
        }

        setContentView(R.layout.activity_auth)

        val tilLogin = findViewById<TextInputLayout>(R.id.til_login)
        val etLogin = findViewById<TextInputEditText>(R.id.user_login)
        val btnLogin = findViewById<MaterialButton>(R.id.button_input)
        val progress = findViewById<ProgressBar>(R.id.progress_bar)

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
                            val user = state.user
                            when (user) {
                                is User.regularUser -> {
                                    SessionManager.save(this@AuthActivity,
                                        isAdmin = false,
                                        userId = user.id,
                                        apartment = user.apartmentNumber,
                                        entrance = user.entrance)
                                    navigateToCore(false, user.id, user.apartmentNumber, user.entrance)
                                }
                                is User.adminUser -> {
                                    SessionManager.save(this@AuthActivity,
                                        isAdmin = true,
                                        userId = user.id,
                                        apartment = user.admLogin,
                                        entrance = "")
                                    navigateToCore(true, user.id, user.admLogin, "")
                                }
                            }
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

    private fun navigateToCore(isAdmin: Boolean, userId: String, apartment: String, entrance: String) {
        startActivity(Intent(this, CoreActivity::class.java).apply {
            putExtra(CoreActivity.EXTRA_IS_ADMIN, isAdmin)
            putExtra(CoreActivity.EXTRA_USER_ID, userId)
            putExtra(CoreActivity.EXTRA_APARTMENT, apartment)
            putExtra(CoreActivity.EXTRA_ENTRANCE, entrance)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}
