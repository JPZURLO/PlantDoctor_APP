package com.joao.plantdoctor.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.joao.plantdoctor.R
import com.joao.plantdoctor.adapter.EditUserViewPagerAdapter
import com.joao.plantdoctor.models.User

class EditUserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        // Pega o objeto User que foi enviado pela tela anterior
        val user = intent.getParcelableExtra<User>("USER_EXTRA")

        // TESTE DE DIAGNÓSTICO: Verificamos se o usuário chegou
        if (user == null) {
            Log.e("EditUserActivity", "ERRO FATAL: Objeto User não foi recebido pela Intent.")
            Toast.makeText(this, "Erro ao carregar dados do usuário.", Toast.LENGTH_LONG).show()
            finish() // Fecha a tela se não houver dados
            return
        }
        Log.d("EditUserActivity", "Usuário recebido com sucesso: ${user.name}")

        // Configuração da Toolbar (barra de título)
        val toolbar = findViewById<Toolbar>(R.id.toolbar_edit_user)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Editar: ${user.name}"

        // Encontra os componentes de layout
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)

        // Cria o adapter que vai gerenciar as abas, passando o usuário para ele
        val adapter = EditUserViewPagerAdapter(this, user)
        viewPager.adapter = adapter

        // Conecta o TabLayout com o ViewPager e define os nomes das abas
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Informações"
                1 -> "Histórico"
                else -> null
            }
        }.attach()
    }

    // Função para fazer o botão de "voltar" na toolbar funcionar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}