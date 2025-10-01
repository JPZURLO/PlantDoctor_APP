package com.joao.plantdoctor.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joao.plantdoctor.CultureAdapter
import com.joao.plantdoctor.R
// ✅✅✅ ADICIONE ESTE IMPORT QUE ESTAVA FALTANDO ✅✅✅
import com.joao.plantdoctor.viewmodel.CultureViewModel


class CultureSelectionActivity : AppCompatActivity() {

    // Esta linha agora funcionará
    private val viewModel: CultureViewModel by viewModels()
    private lateinit var adapter: CultureAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_culture_selection)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_cultures)
        val btnConfirm = findViewById<Button>(R.id.btn_confirm_cultures)

        setupRecyclerView(recyclerView)
        setupObservers()

        viewModel.fetchAllCultures()

        btnConfirm.setOnClickListener {
            val selectedIds = adapter.getSelectedCultures().map { it.id }
            btnConfirm.isEnabled = false
            viewModel.saveUserCultures(selectedIds)
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        adapter = CultureAdapter(emptyList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 2)
    }

    private fun setupObservers() {
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

        viewModel.saveResult.observe(this) { result ->
            result.onSuccess { apiResponse ->
                Log.d("CULTURE_DEBUG", "Culturas guardadas com sucesso! Mensagem: ${apiResponse.message}. Navegando para a Home.")
                Toast.makeText(this, "Culturas guardadas com sucesso!", Toast.LENGTH_SHORT).show()

                // Substitua HomeActivity::class.java pela sua tela principal se o nome for diferente
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