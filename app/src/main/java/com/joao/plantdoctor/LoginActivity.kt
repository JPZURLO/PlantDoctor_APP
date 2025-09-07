package com.joao.plantdoctor

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
            // Usando onSuccess e onFailure para tratar o resultado de forma segura.
            result.onSuccess { loginResponse ->
                // Login bem-sucedido.
                Toast.makeText(this, "Login bem-sucedido! ${loginResponse.message}", Toast.LENGTH_SHORT).show()

                // TODO: Salvar o token de autenticação e navegar para a tela principal.
                // val intent = Intent(this, MainActivity::class.java)
                // startActivity(intent)
                // finish() // Fecha a LoginActivity para não poder voltar.
            }
            result.onFailure { error ->
                // Falha no login. Mostra a mensagem de erro.
                Toast.makeText(this, "Erro: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}

