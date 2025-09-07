// CreateNewPasswordActivity.kt
package com.joao.plantdoctor

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.joao.plantdoctor.models.ResetPasswordRequest

class CreateNewPasswordActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    // ✅ Variável para guardar o token real que virá do link
    private var resetToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_password)

        // ✅ PASSO 1: Chamar a função para extrair o token do link que abriu esta Activity
        handleIntent(intent)

        // Se, por algum motivo, a Activity abriu sem um token, não podemos continuar.
        if (resetToken == null) {
            Toast.makeText(this, "Token de redefinição inválido ou ausente.", Toast.LENGTH_LONG).show()
            finish() // Fecha a Activity
            return // Para a execução do onCreate
        }

        val etNewPassword = findViewById<EditText>(R.id.et_new_password)
        val etConfirmPassword = findViewById<EditText>(R.id.et_confirm_password)
        val btnSavePassword = findViewById<Button>(R.id.btn_save_password)

        btnSavePassword.setOnClickListener {
            val newPassword = etNewPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (newPassword.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (newPassword == confirmPassword) {
                    // ✅ PASSO 2: Usar o token real (resetToken) em vez do placeholder
                    val request = ResetPasswordRequest(token = resetToken!!, newPassword = newPassword)
                    authViewModel.resetPassword(request)
                } else {
                    Toast.makeText(this, "As senhas não coincidem.", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            }
        }

        setupObservers()
    }

    /**
     * ✅ PASSO 3: Adicionar a função que lê o link (Intent) e extrai o token.
     */
    private fun handleIntent(intent: Intent?) {
        val action: String? = intent?.action
        val data: Uri? = intent?.data

        // Verifica se a Activity foi aberta por um link (ACTION_VIEW)
        if (Intent.ACTION_VIEW == action && data != null) {
            // Extrai o parâmetro chamado "token" da URL
            // Ex: https://.../reset?token=ESTE_VALOR_AQUI
            resetToken = data.getQueryParameter("token")
        }
    }

    private fun setupObservers() {
        authViewModel.resetPasswordResult.observe(this) { result ->
            result.onSuccess { apiResponse ->
                // Usa a mensagem que vem da API
                Toast.makeText(this, apiResponse.message, Toast.LENGTH_LONG).show()

                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            result.onFailure { error ->
                Toast.makeText(this, "Erro: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}