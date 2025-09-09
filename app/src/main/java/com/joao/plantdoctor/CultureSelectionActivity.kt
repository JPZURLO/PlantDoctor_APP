package com.joao.plantdoctor

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joao.plantdoctor.models.Culture
import android.util.Log

class CultureSelectionActivity : AppCompatActivity() {

    // Injeta o ViewModel que irá gerir a comunicação com a API
    private val viewModel: CultureViewModel by viewModels()
    private lateinit var adapter: CultureAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_culture_selection)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_cultures)
        val btnConfirm = findViewById<Button>(R.id.btn_confirm_cultures)

        setupRecyclerView(recyclerView)
        setupObservers()

        // Inicia a busca pela lista de todas as culturas disponíveis na API
        viewModel.fetchAllCultures()

        btnConfirm.setOnClickListener {
            val selectedIds = adapter.getSelectedCultures().map { it.id }
            // Desativa o botão para evitar cliques múltiplos
            btnConfirm.isEnabled = false
            // Pede ao ViewModel para guardar as culturas selecionadas
            viewModel.saveUserCultures(selectedIds)
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        // Inicializa o adapter com uma lista vazia. Ele será preenchido com os dados da API.
        adapter = CultureAdapter(emptyList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 2)
    }

    // Em CultureSelectionActivity.kt

    private fun setupObservers() {
        // Este observador já está correto e com logs
        viewModel.allCultures.observe(this) { result ->
            result.onSuccess { cultures ->
                Log.d("CULTURE_DEBUG", "Culturas recebidas com sucesso: ${cultures.size} itens")
                adapter.updateCultures(cultures)
            }
            result.onFailure { error ->
                Log.e("CULTURE_DEBUG", "Falha ao buscar culturas: ${error.message}", error)
                Toast.makeText(this, "Erro ao buscar culturas: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }

        // ==========================================================
        // ▼▼▼ ADICIONE LOGS A ESTE OBSERVADOR ▼▼▼
        // ==========================================================
        viewModel.saveResult.observe(this) { result ->
            result.onSuccess { apiResponse ->
                Log.d("CULTURE_DEBUG", "Culturas guardadas com sucesso! Mensagem: ${apiResponse.message}. A navegar para a Home.")
                Toast.makeText(this, "Culturas guardadas com sucesso!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            result.onFailure { error ->
                Log.e("CULTURE_DEBUG", "Falha ao GUARDAR culturas: ${error.message}", error)
                Toast.makeText(this, "Erro ao guardar: ${error.message}", Toast.LENGTH_LONG).show()
                findViewById<Button>(R.id.btn_confirm_cultures).isEnabled = true
            }
        }
    }
}

