package com.joao.plantdoctor.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.joao.plantdoctor.R
import com.joao.plantdoctor.fragments.HomeFragment
import com.joao.plantdoctor.fragments.CulturesFragment
import com.joao.plantdoctor.fragments.DiagnoseFragment
import com.joao.plantdoctor.fragments.NewsFragment
import com.joao.plantdoctor.fragments.WeatherFragment
import android.view.MenuItem // Adicionar este import

class HomeActivity : AppCompatActivity() {

    private val homeFragment = HomeFragment()
    private val culturesFragment = CulturesFragment()
    private val diagnoseFragment = DiagnoseFragment()
    private val newsFragment = NewsFragment()
    private val weatherFragment = WeatherFragment()

    private var activeFragment: Fragment = homeFragment
    private lateinit var bottomNav: BottomNavigationView // Declarar para uso em listeners

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragment_container, homeFragment, "1").show(homeFragment)
            add(R.id.fragment_container, culturesFragment, "2").hide(culturesFragment)
            add(R.id.fragment_container, diagnoseFragment, "3").hide(diagnoseFragment)
            add(R.id.fragment_container, newsFragment, "4").hide(newsFragment)
            add(R.id.fragment_container, weatherFragment, "5").hide(weatherFragment)
        }.commit()

        bottomNav = findViewById(R.id.bottom_navigation)

        // ✅ CORREÇÃO 1: Trata o item Culturas como AÇÃO SEPARADA, fora do switch
        bottomNav.setOnItemSelectedListener(::handleBottomNavigation)

        // ✅ CORREÇÃO 2: Adiciona um Listener para lidar com o clique
        // Se o item de culturas não for um fragmento, ele é tratado aqui.
        bottomNav.menu.findItem(R.id.nav_cultures).setOnMenuItemClickListener {
            startActivity(Intent(this, ManageCulturesActivity::class.java))
            // Retorna TRUE para que o BottomNavigationView saiba que a ação foi tratada.
            true
        }
    }

    // ✅ CORREÇÃO 3: Nova função que trata a navegação de Fragmentos
    private fun handleBottomNavigation(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_home -> {
                switchFragment(homeFragment)
                true
            }
            R.id.nav_weather -> {
                switchFragment(weatherFragment)
                true
            }
            R.id.nav_diagnose -> {
                switchFragment(diagnoseFragment)
                true
            }
            R.id.nav_news -> {
                switchFragment(newsFragment)
                true
            }
            // Retorna FALSE para o item Culturas, permitindo que o OnMenuItemClickListener o trate
            R.id.nav_cultures -> false
            else -> false
        }
    }


    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().hide(activeFragment).show(fragment).commit()
        activeFragment = fragment
    }
}