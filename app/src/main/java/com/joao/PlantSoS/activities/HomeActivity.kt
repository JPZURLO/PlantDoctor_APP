package com.joao.PlantSoS.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View // ✅ ADICIONAR IMPORT
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.joao.PlantSoS.R
import com.joao.PlantSoS.fragments.CulturesFragment
import com.joao.PlantSoS.fragments.DiagnoseFragment
import com.joao.PlantSoS.fragments.HomeFragment
import com.joao.PlantSoS.fragments.NewsFragment
import com.joao.PlantSoS.fragments.WeatherFragment

class HomeActivity : AppCompatActivity() {

    // Instâncias dos fragmentos
    private val homeFragment = HomeFragment()
    private val culturesFragment = CulturesFragment()
    private val diagnoseFragment = DiagnoseFragment()
    private val newsFragment = NewsFragment()
    private val weatherFragment = WeatherFragment()

    private var activeFragment: Fragment = homeFragment
    private lateinit var bottomNav: BottomNavigationView // Referência à barra de navegação

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

        bottomNav = findViewById(R.id.bottom_navigation) // Encontra a barra

        bottomNav.setOnItemSelectedListener { item ->
            // A navegação principal é tratada aqui
            handleNavigation(item.itemId)
        }

        // ✅ NOVO: Ouve as mudanças da "pilha de retorno" (back stack)
        // Isto esconde a barra de navegação quando se entra num sub-ecrã
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount > 0) {
                // Estamos num sub-ecrã (ex: Dúvidas)
                bottomNav.visibility = View.GONE
            } else {
                // Estamos num ecrã principal (ex: Home)
                bottomNav.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Função privada que efetivamente troca os fragmentos (TABS)
     */
    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().hide(activeFragment).show(fragment).commit()
        activeFragment = fragment
    }

    /**
     * Lógica central de navegação (TABS)
     */
    private fun handleNavigation(menuItemId: Int): Boolean {
        when (menuItemId) {
            R.id.nav_home -> {
                switchFragment(homeFragment)
                return true
            }
            R.id.nav_cultures -> {
                switchFragment(culturesFragment)
                return true
            }
            R.id.nav_diagnose -> {
                switchFragment(diagnoseFragment)
                return true
            }
            R.id.nav_news -> {
                switchFragment(newsFragment)
                return true
            }
            else -> return false
        }
    }

    /**
     * Permite que um fragmento filho peça à Activity para mudar de aba.
     */
    fun navigateToTab(menuItemId: Int) {
        // Tenta tratar a navegação
        val handled = handleNavigation(menuItemId)
        if (handled) {
            // Se a navegação foi bem-sucedida, atualiza o item selecionado na barra
            bottomNav.selectedItemId = menuItemId
        }
    }

    // --- ✅ NOVA FUNÇÃO PÚBLICA (PARA SUB-ECRÃS) ---
    /**
     * Permite que um fragmento filho abra um NOVO fragmento (sub-ecrã)
     * por cima do ecrã atual.
     */
    fun navigateTo(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            // Substitui o container
            .replace(R.id.fragment_container, fragment)
            // Adiciona à pilha para o botão "Voltar" funcionar
            .addToBackStack(null)
            .commit()
    }
}

