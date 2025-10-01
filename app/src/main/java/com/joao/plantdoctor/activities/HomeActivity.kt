package com.joao.plantdoctor.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.joao.plantdoctor.R
import com.joao.plantdoctor.fragments.HomeFragment
// Importe todos os fragmentos necessários. VOCÊ PRECISA CRIAR ESTAS CLASSES.
import com.joao.plantdoctor.fragments.CulturesFragment
import com.joao.plantdoctor.fragments.DiagnoseFragment
import com.joao.plantdoctor.fragments.NewsFragment
import com.joao.plantdoctor.fragments.WeatherFragment // O WeatherFragment que já havíamos corrigido.

class HomeActivity : AppCompatActivity() {

    // 1. DECLARAÇÃO: Instancie todos os fragmentos.
    private val homeFragment = HomeFragment()
    private val culturesFragment = CulturesFragment() // NOVO
    private val diagnoseFragment = DiagnoseFragment() // NOVO
    private val newsFragment = NewsFragment() // NOVO
    private val weatherFragment = WeatherFragment() // JÁ EXISTENTE

    private var activeFragment: Fragment = homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // 2. ADIÇÃO: Adicione todos os fragmentos ao FragmentManager (esconde todos exceto o primeiro).
        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragment_container, homeFragment, "1").show(homeFragment)
            add(R.id.fragment_container, culturesFragment, "2").hide(culturesFragment) // NOVO
            add(R.id.fragment_container, diagnoseFragment, "3").hide(diagnoseFragment) // NOVO
            add(R.id.fragment_container, newsFragment, "4").hide(newsFragment) // NOVO
            add(R.id.fragment_container, weatherFragment, "5").hide(weatherFragment) // JÁ EXISTENTE
        }.commit()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> switchFragment(homeFragment)
                // 3. ATIVAÇÃO: Referencie as novas variáveis aqui
                R.id.nav_cultures -> switchFragment(culturesFragment)
                R.id.nav_diagnose -> switchFragment(diagnoseFragment)
                R.id.nav_news -> switchFragment(newsFragment)
                R.id.nav_weather -> switchFragment(weatherFragment)
                // R.id.nav_profile foi removido e substituído
            }
            true
        }
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().hide(activeFragment).show(fragment).commit()
        activeFragment = fragment
    }
}