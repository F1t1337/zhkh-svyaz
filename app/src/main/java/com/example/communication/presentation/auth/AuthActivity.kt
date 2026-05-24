package com.example.communication.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.communication.R
import com.example.communication.data.repositories.AuthRepositoryImpl
import com.example.communication.domain.usecases.auth.Login
import com.example.communication.presentation.regular.CoreActivity
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(
            login = Login(AuthRepositoryImpl())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val userLogin: EditText = findViewById(R.id.user_login)
        val userPass: EditText  = findViewById(R.id.user_password)
        val inputBtn: Button    = findViewById(R.id.button_input)

        inputBtn.setOnClickListener {
            viewModel.login(
                identifier = userLogin.text.toString(),
                password   = userPass.text.toString()
            )
        }

        collectUiState()
    }

    private fun collectUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    handleState(state)
                }
            }
        }
    }

    private fun handleState(state: AuthUiState) {
        when (state) {
            is AuthUiState.Idle -> {
            }
            is AuthUiState.Loading -> {
            }
            is AuthUiState.Success -> {
                val intent = Intent(this, CoreActivity::class.java)
                startActivity(intent)
                finish()
            }
            is AuthUiState.Error -> {
                Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
