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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {

    private val authRepo by lazy { SupabaseAuthRepository() }

    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(login = Login(authRepo))
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
        val btnChangePassword = findViewById<MaterialButton>(R.id.btn_change_password)

        btnLogin.setOnClickListener {
            val identifier = etLogin.text?.toString()?.trim() ?: ""
            val password = findViewById<TextInputEditText>(R.id.user_password).text?.toString() ?: ""
            viewModel.login(identifier, password)
        }

        btnChangePassword.setOnClickListener {
            showChangePasswordSheet()
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

    private fun showChangePasswordSheet() {
        val sheet = BottomSheetDialog(this)
        val sheetView = layoutInflater.inflate(R.layout.bottom_sheet_change_password, null)
        sheet.setContentView(sheetView)

        val etLogin = sheetView.findViewById<TextInputEditText>(R.id.et_login_cp)
        val etPassport = sheetView.findViewById<TextInputEditText>(R.id.et_passport_cp)
        val etNewPw = sheetView.findViewById<TextInputEditText>(R.id.et_new_password_cp)
        val etNewPwRepeat = sheetView.findViewById<TextInputEditText>(R.id.et_new_password_repeat_cp)

        sheetView.findViewById<MaterialButton>(R.id.btn_confirm_change_password).setOnClickListener {
            val login = etLogin.text?.toString()?.trim() ?: ""
            val passport = etPassport.text?.toString()?.trim() ?: ""
            val newPw = etNewPw.text?.toString() ?: ""
            val newPwRepeat = etNewPwRepeat.text?.toString() ?: ""

            if (login.isBlank() || passport.isBlank() || newPw.isBlank() || newPwRepeat.isBlank()) {
                Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (newPw != newPwRepeat) {
                Toast.makeText(this, R.string.error_passwords_mismatch, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (newPw.length < 6) {
                Toast.makeText(this, R.string.error_password_too_short, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val result = authRepo.changePassword(login, passport, newPw)
                result.fold(
                    onSuccess = {
                        Toast.makeText(this@AuthActivity, R.string.password_changed, Toast.LENGTH_SHORT).show()
                        sheet.dismiss()
                    },
                    onFailure = { e ->
                        Toast.makeText(this@AuthActivity, e.message ?: getString(R.string.error_generic), Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        sheetView.findViewById<MaterialButton>(R.id.btn_cancel_change_password).setOnClickListener {
            sheet.dismiss()
        }

        sheet.show()
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
