package com.joao.plantdoctor

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.joao.plantdoctor.ManageCulturesActivity

class HomeActivity : AppCompatActivity() {

    // Injeta o ViewModel para esta Activity
    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var recyclerViewCultures: RecyclerView
    private lateinit var cultureAdapter: CultureHomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val fabDiagnose = findViewById<FloatingActionButton>(R.id.fab_diagnose)
        val btnManageCultures = findViewById<TextView>(R.id.btn_manage_cultures)
        recyclerViewCultures = findViewById(R.id.recycler_view_my_cultures)

        setupRecyclerView()
        setupObservers()

        fabDiagnose.setOnClickListener {
            Toast.makeText(this, "Abrir câmara para diagnóstico...", Toast.LENGTH_SHORT).show()
        }

        btnManageCultures.setOnClickListener {
            startActivity(Intent(this, ManageCulturesActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        // Sempre que a tela ficar visível, busca os dados mais recentes
        homeViewModel.fetchMyCultures()
    }

    private fun setupRecyclerView() {
        cultureAdapter = CultureHomeAdapter(emptyList()) // Começa com uma lista vazia
        recyclerViewCultures.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewCultures.adapter = cultureAdapter
    }

    private fun setupObservers() {
        homeViewModel.myCultures.observe(this) { result ->
            result.onSuccess { cultures ->
                // Atualiza a lista no adapter com os dados vindos da API
                cultureAdapter.updateCultures(cultures)
            }
            result.onFailure { error ->
                Toast.makeText(this, "Erro ao buscar culturas: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}

