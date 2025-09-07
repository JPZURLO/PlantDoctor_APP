package com.joao.plantdoctor

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    // Tempo que a splash screen ficará visível em milissegundos
    private val SPLASH_TIME_OUT: Long = 3000 // 3 segundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Usa uma coroutine para esperar um tempo e depois navegar para a próxima tela.
        // O lifecycleScope garante que, se o usuário fechar o app, a tarefa é cancelada.
        lifecycleScope.launch {
            // Espera pelo tempo definido
            delay(SPLASH_TIME_OUT)

            // Após o tempo, cria o Intent para a LoginActivity
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)

            // Fecha a MainActivity para que o usuário não possa voltar para ela
            // pressionando o botão "Voltar".
            finish()
        }
    }
}

