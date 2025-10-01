package com.joao.plantdoctor

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener(navListener)

        // Carrega o fragmento inicial
        if (savedInstanceState == null) {
            openFragment(HomeFragment())
        }
    }

    private val navListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
                openFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_cultures -> {
                startActivity(Intent(this, ManageCulturesActivity::class.java))
                return@OnNavigationItemSelectedListener false // Boa prática
            }
            R.id.nav_diagnose -> {
                // TODO: Iniciar a Activity ou Fragment de Diagnóstico
                Toast.makeText(this, "Diagnóstico clicado!", Toast.LENGTH_SHORT).show()
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_news -> {
                // TODO: Criar e abrir o Fragment de Notícias
                Toast.makeText(this, "Notícias clicado!", Toast.LENGTH_SHORT).show()
                return@OnNavigationItemSelectedListener true
            }
            // ✅ NOVO CASE ADICIONADO PARA O TEMPO
            R.id.nav_weather -> {
                openFragment(WeatherFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}