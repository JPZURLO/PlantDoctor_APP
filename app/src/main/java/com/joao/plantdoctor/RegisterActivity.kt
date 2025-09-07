package com.joao.plantdoctor

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.joao.plantdoctor.models.UserRequest

class RegisterActivity : AppCompatActivity() {

    // Utiliza o AuthViewModel partilhado para a lógica de negócio.
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etName = findViewById<EditText>(R.id.et_name)
        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnRegister = findViewById<Button>(R.id.btn_register)
        val tvLogin = findViewById<TextView>(R.id.tv_login)

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                // Cria o objeto de dados do utilizador.
                val user = UserRequest(
                    name = name,
                    email = email,
                    password = password
                )
                // A Activity apenas delega a ação para o ViewModel.
                authViewModel.registerUser(user)
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            }
        }

        tvLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Configura os observadores para que a UI reaja às mudanças de estado no ViewModel.
        setupObservers()
    }

    /**
     * Configura o observador do LiveData para reagir ao resultado da operação de registo.
     */
    private fun setupObservers() {
        authViewModel.registerResult.observe(this) { result ->
            result.onSuccess { successMessage ->
                // ✅ CORREÇÃO 1: Aceda à propriedade .message do objeto
                Toast.makeText(this, successMessage.message, Toast.LENGTH_LONG).show()

                // Navega para a tela de login.
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            result.onFailure { error ->
                // ✅ CORREÇÃO 2: O .show() pertence ao Toast.makeText(...)
                Toast.makeText(this, "Erro no registo: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}



