// RecoveryActivity.kt
package com.joao.plantdoctor.activities

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels // Importe esta classe
import androidx.appcompat.app.AppCompatActivity
import com.joao.plantdoctor.AuthViewModel
import com.joao.plantdoctor.R

class RecoveryActivity : AppCompatActivity() {

    // ✅ PASSO 1: Conectar o AuthViewModel à Activity
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recovery)

        val etEmail = findViewById<EditText>(R.id.et_email_recovery)
        val btnRecover = findViewById<Button>(R.id.btn_recover)
        val tvBackToLogin = findViewById<TextView>(R.id.tv_back_to_login_recovery)

        btnRecover.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                // ✅ PASSO 2: Chamar a função do ViewModel em vez de mostrar um Toast fixo
                // A lógica de mostrar Toast e navegar foi movida para o 'setupObservers'
                authViewModel.requestPasswordReset(email)

            } else {
                Toast.makeText(this, "Por favor, insira um e-mail válido.", Toast.LENGTH_SHORT).show()
            }
        }

        tvBackToLogin.setOnClickListener {
            finish()
        }

        // ✅ PASSO 3: Chamar a função que observa o resultado da API
        setupObservers()
    }

    /**
     * ✅ PASSO 4: Adicionar a função que observa o LiveData do ViewModel
     * Esta função reage ao resultado da chamada de rede.
     */
    private fun setupObservers() {
        authViewModel.requestResetResult.observe(this) { result ->
            result.onSuccess { apiResponse ->
                // Por segurança, a mensagem de sucesso é sempre genérica
                Toast.makeText(this, "Se existir uma conta com este e-mail, um link de recuperação foi enviado.", Toast.LENGTH_LONG).show()
                finish() // Volta para a tela de login após o sucesso
            }
            result.onFailure { error ->
                // Mostra o erro real que veio da API ou da falha de conexão
                Toast.makeText(this, "Erro: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}