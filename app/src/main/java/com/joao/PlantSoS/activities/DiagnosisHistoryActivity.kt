package com.joao.PlantSoS.activities

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels // ⬅️ Importe o by viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.lifecycleScope
import com.joao.PlantSoS.R
import com.joao.PlantSoS.adapter.DiagnosisHistoryAdapter
// import com.joao.PlantSoS.network.RetrofitClient // ⬅️ NÃO USE MAIS
import com.joao.PlantSoS.viewmodel.CultureDetailsViewModel // ⬅️ Use o ViewModel
import kotlinx.coroutines.launch

class DiagnosisHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DiagnosisHistoryAdapter
    // Reutilize o ViewModel que já tem a lógica
    private val viewModel: CultureDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diagnosis_history)

        recyclerView = findViewById(R.id.recycler_view_diagnosis_history_list)
        adapter = DiagnosisHistoryAdapter()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // 1. Observe os dados do ViewModel
        viewModel.diagnosisHistory.observe(this) { historyList ->
            adapter.submitList(historyList)
            if (historyList.isEmpty()) {
                Toast.makeText(this, "Nenhum histórico encontrado.", Toast.LENGTH_SHORT).show()
            }
        }

        // 2. Peça ao ViewModel para carregar os dados
        loadDiagnosisHistory()
    }

    private fun loadDiagnosisHistory() {
        val token = getToken()
        val cultureId = intent.getIntExtra("CULTURE_ID", -1) // Pega o ID da tela anterior

        if (token == null || cultureId == -1) {
            Toast.makeText(this, "Erro de autenticação ou ID da cultura.", Toast.LENGTH_SHORT).show()
            return
        }

        // Pede ao ViewModel para buscar (ele vai rodar na coroutine)
        viewModel.fetchAllCultureData(token, cultureId)
    }

    // Função para pegar o token
    private fun getToken(): String? {
        val sharedPrefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        return sharedPrefs.getString("AUTH_TOKEN", null)
    }
}