package com.joao.plantdoctor

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var btnNext: Button
    private val handler = Handler(Looper.getMainLooper())
    private var autoScrollRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        btnNext = findViewById(R.id.btn_next)

        val adapter = OnboardingAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == adapter.itemCount - 1) { // Última página
                    stopAutoScroll()
                    btnNext.text = "Aguarde enquanto carregamos suas informações"
                    btnNext.isEnabled = false
                    Handler(Looper.getMainLooper()).postDelayed({
                        btnNext.text = "AVANÇAR"
                        btnNext.isEnabled = true
                    }, 3000)
                } else {
                    btnNext.text = "AVANÇAR"
                    btnNext.isEnabled = true
                    startAutoScroll()
                }
            }
        })

        btnNext.setOnClickListener {
            if (viewPager.currentItem < adapter.itemCount - 1) {
                viewPager.currentItem += 1
            } else {
                // ✅ ALTERAÇÃO APLICADA: Agora direciona para a HomeActivity
                // Nota: Isto irá saltar a tela de seleção de culturas.
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

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

