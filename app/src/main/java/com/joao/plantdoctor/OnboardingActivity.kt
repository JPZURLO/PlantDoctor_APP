package com.joao.plantdoctor

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels // Certifique-se de que este import está presente
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {

    // --- Componentes de UI e Handlers ---
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var btnNext: Button
    private val handler = Handler(Looper.getMainLooper())
    private var autoScrollRunnable: Runnable? = null

    // ✅ PASSO 1: Injetar o ViewModel que busca as culturas
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        // --- Inicialização da UI ---
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        btnNext = findViewById(R.id.btn_next)

        val adapter = OnboardingAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()

        // ✅ PASSO 2: Configurar o observador que vai reagir ao resultado da API
        setupObservers()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == adapter.itemCount - 1) { // Última página
                    btnNext.text = "AVANÇAR"
                    btnNext.isEnabled = true
                    stopAutoScroll()
                } else {
                    btnNext.text = "AVANÇAR"
                    btnNext.isEnabled = true
                    startAutoScroll()
                }
            }
        })

        // ✅ PASSO 3: Modificar o clique do botão
        btnNext.setOnClickListener {
            if (viewPager.currentItem < adapter.itemCount - 1) {
                viewPager.currentItem += 1
            } else {
                // Na última página, ao invés de navegar, pede a verificação
                btnNext.text = "A verificar..."
                btnNext.isEnabled = false
                viewModel.fetchMyCultures() // Dispara a chamada à API
            }
        }
    }

    // ✅ PASSO 4: Criar a função que observa os resultados do ViewModel
    private fun setupObservers() {
        viewModel.myCultures.observe(this) { result ->
            result.onSuccess { userCultures ->
                // A decisão de navegação é tomada aqui!
                if (userCultures.isEmpty()) {
                    // Se a lista de culturas for VAZIA, vai para a seleção
                    startActivity(Intent(this, CultureSelectionActivity::class.java))
                } else {
                    // Se o usuário JÁ TIVER culturas, vai para a home
                    startActivity(Intent(this, HomeActivity::class.java))
                }
                finish() // Fecha a OnboardingActivity
            }
            result.onFailure { error ->
                Toast.makeText(this, "Erro ao verificar dados: ${error.message}", Toast.LENGTH_LONG).show()
                // Em caso de falha, é mais seguro enviar para a tela de seleção
                startActivity(Intent(this, CultureSelectionActivity::class.java))
                finish()
            }
        }
    }

    // --- Funções de Auto-Scroll (sem alterações) ---
    private fun startAutoScroll() {
        stopAutoScroll()
        autoScrollRunnable = Runnable {
            val currentItem = viewPager.currentItem
            if (currentItem < (viewPager.adapter?.itemCount ?: 0) - 1) {
                viewPager.currentItem = currentItem + 1
            } else {
                stopAutoScroll()
            }
        }
        handler.postDelayed(autoScrollRunnable!!, 4000)
    }

    private fun stopAutoScroll() {
        autoScrollRunnable?.let { handler.removeCallbacks(it) }
    }

    override fun onResume() {
        super.onResume()
        if (viewPager.currentItem != (viewPager.adapter?.itemCount ?: 0) - 1) {
            startAutoScroll()
        }
    }

    override fun onPause() {
        super.onPause()
        stopAutoScroll()
    }
}