package com.joao.plantdoctor.activities
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.joao.plantdoctor.AuthViewModel
import com.joao.plantdoctor.R
import com.joao.plantdoctor.models.LoginRequest

class LoginActivity : AppCompatActivity() {

    // Injeta o AuthViewModel, garantindo que ele sobrevive a mudanças de configuração.
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val tvRegister = findViewById<TextView>(R.id.tv_register)
        val tvForgotPassword = findViewById<TextView>(R.id.tv_forgot_password)

        // Configura o clique do botão de login para chamar o ViewModel.
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Cria o objeto de dados e passa a responsabilidade para o ViewModel.
                val loginData = LoginRequest(email = email, password = password)
                authViewModel.loginUser(loginData)
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            }
        }

        // Configura os observadores para que a UI reaja às mudanças no ViewModel.
        setupObservers()

        // Configura a navegação para as outras telas.
        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, RecoveryActivity::class.java))
        }
    }

    /**
     * Configura os observadores do LiveData para reagir ao resultado da operação de login.
     */
    private fun setupObservers() {
        authViewModel.loginResult.observe(this) { result ->
            result.onSuccess { loginResponse ->

                // ==========================================================
                // ▼▼▼ ADICIONE ESTA LINHA PARA IMPRIMIR O TOKEN ▼▼▼
                // ==========================================================
                Log.d("TOKEN_DEBUG", "Token Recebido: ${loginResponse.token}")
                // ==========================================================

                // Login bem-sucedido.
                Toast.makeText(this, loginResponse.message, Toast.LENGTH_SHORT).show()

                // O resto do seu código para guardar o token e navegar...
                val sharedPrefs = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                with(sharedPrefs.edit()) {
                    putString("AUTH_TOKEN", loginResponse.token)
                    apply()
                }

                val intent = Intent(this, OnboardingActivity::class.java)
                startActivity(intent)
                finish()
            }
            result.onFailure { error ->
                Toast.makeText(this, "Erro: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
