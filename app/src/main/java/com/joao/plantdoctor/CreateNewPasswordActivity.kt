// CreateNewPasswordActivity.kt
package com.joao.plantdoctor

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView // ✅ Importar TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.joao.plantdoctor.models.ResetPasswordRequest

class CreateNewPasswordActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    // Variável para guardar o token que virá do link
    private var resetToken: String? = null
    // ✅ Variável para guardar o e-mail que virá do link
    private var userEmail: String? = null

    // ✅ Declaração do novo componente de UI
    private lateinit var tvUserEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_password)

        // ✅ PASSO 1: Chamar a função para extrair os dados do link que abriu esta Activity
        handleIntent(intent)

        // Se, por algum motivo, a Activity abriu sem um token ou e-mail, não podemos continuar.
        if (resetToken == null || userEmail == null) {
            Toast.makeText(this, "Link de redefinição inválido ou dados ausentes.", Toast.LENGTH_LONG).show()
            finish() // Fecha a Activity
            return   // Para a execução do onCreate
        }

        // ✅ PASSO 2: Inicializar os componentes de UI
        tvUserEmail = findViewById(R.id.tv_user_email)
        val etNewPassword = findViewById<EditText>(R.id.et_new_password)
        val etConfirmPassword = findViewById<EditText>(R.id.et_confirm_password)
        val btnSavePassword = findViewById<Button>(R.id.btn_save_password)

        // ✅ PASSO 3: Exibir o e-mail do usuário no campo bloqueado
        tvUserEmail.text = userEmail

        btnSavePassword.setOnClickListener {
            val newPassword = etNewPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (newPassword.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (newPassword == confirmPassword) {
                    // Usar o token real (resetToken) que foi extraído do link
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
     * ✅ PASSO 4: MODIFICADO - Função que lê o link (Intent) e extrai o TOKEN e o E-MAIL.
     */
    private fun handleIntent(intent: Intent?) {
        val action: String? = intent?.action
        val data: Uri? = intent?.data

        // Verifica se a Activity foi aberta por um link (ACTION_VIEW)
        if (Intent.ACTION_VIEW == action && data != null) {
            // Extrai o parâmetro chamado "token" da URL
            // Ex: plantdoctor://reset-password?token=ESTE_VALOR&email=...
            resetToken = data.getQueryParameter("token")

            // ✅ Extrai o novo parâmetro chamado "email" da URL
            userEmail = data.getQueryParameter("email")
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