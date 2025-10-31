package com.joao.PlantSoS.activities

import ManageCulturesAdapter
// ✅ ADICIONE ESTES IMPORTS
import android.content.Intent
import android.os.Bundle
import android.widget.Button // Import para o botão
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joao.PlantSoS.R
import com.joao.PlantSoS.models.Culture
import com.joao.PlantSoS.viewmodel.CultureViewModel

// Supondo que sua tela Home se chama HomeActivity
// import com.joao.PlantSoS.activities.HomeActivity

class ManageCulturesActivity : AppCompatActivity() {

    private val viewModel: CultureViewModel by viewModels()
    private lateinit var adapter: ManageCulturesAdapter

    private var allCultures: List<Culture>? = null
    private var myCultureIds: Set<Int>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_cultures)

        setupRecyclerView()
        setupObservers()

        // ✅ PASSO 2: CONFIGURAR O CLIQUE DO BOTÃO SALVAR
        // (Certifique-se que o ID no XML é 'btn_save_cultures')
        val saveButton = findViewById<Button>(R.id.btn_save_cultures)
        saveButton.setOnClickListener {
            // Só salva no ViewModel quando o usuário clicar no botão final
            myCultureIds?.let { ids ->
                viewModel.saveUserCultures(ids.toList())
            }
        }

        viewModel.fetchAllCultures()
        viewModel.fetchUserCultures()
    }

    private fun setupRecyclerView() {
        // ... seu setupRecyclerView não muda ...
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_manage_cultures)
        adapter = ManageCulturesAdapter(
            cultures = emptyList(),
            onAddClick = { culture ->
                updateUserCultures(culture, add = true)
            },
            onRemoveClick = { culture ->
                updateUserCultures(culture, add = false)
            }
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupObservers() {
        // ... seu observer 'allCultures' não muda ...
        viewModel.allCultures.observe(this) { result ->
            result.onSuccess { cultures ->
                this.allCultures = cultures
                combineAndDisplayLists()
            }
            result.onFailure { error ->
                Toast.makeText(this, "Erro ao buscar culturas: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }

        // ... seu observer 'userCultures' não muda ...
        viewModel.userCultures.observe(this) { result ->
            result.onSuccess { myCultures ->
                this.myCultureIds = myCultures.map { it.id }.toSet()
                combineAndDisplayLists()
            }
            result.onFailure { error ->
                Toast.makeText(this, "Erro ao buscar suas culturas: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }

        // ✅ PASSO 3: ATUALIZAR O OBSERVER DE 'saveResult'
        viewModel.saveResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Lista de culturas atualizada!", Toast.LENGTH_SHORT).show()

                // ****** AQUI ESTÁ A NAVEGAÇÃO ******
                // Substitua 'HomeActivity::class.java' pela sua tela principal
                val intent = Intent(this, HomeActivity::class.java)

                // Limpa a pilha de activities (para o usuário não "voltar" para cá)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

                // 'finish()' também funciona se você só quiser fechar esta tela
                // finish()
            }
            result.onFailure { error ->
                Toast.makeText(this, "Erro ao atualizar: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun combineAndDisplayLists() {
        if (allCultures != null && myCultureIds != null) {
            val combinedList = allCultures!!.map { culture ->
                culture.copy(isSelected = myCultureIds!!.contains(culture.id))
            }
            adapter.updateCultures(combinedList)
        }
    }

    // ✅ PASSO 1: SIMPLIFICAR A FUNÇÃO 'updateUserCultures'
    private fun updateUserCultures(changedCulture: Culture, add: Boolean) {
        val currentIds = myCultureIds?.toMutableSet() ?: mutableSetOf()

        if (add) {
            currentIds.add(changedCulture.id)
        } else {
            currentIds.remove(changedCulture.id)
        }

        // Apenas atualiza a variável local e a UI
        myCultureIds = currentIds
        combineAndDisplayLists()

        // REMOVA A LINHA ABAIXO (ela salvava a cada clique)
        // viewModel.saveUserCultures(currentIds.toList())
    }
}