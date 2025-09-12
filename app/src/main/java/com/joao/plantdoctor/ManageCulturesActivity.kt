package com.joao.plantdoctor

import ManageCulturesAdapter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joao.plantdoctor.models.Culture

class ManageCulturesActivity : AppCompatActivity() {

    // 1. Injeta o ViewModel que lida com a lógica de culturas
    private val viewModel: CultureViewModel by viewModels()
    private lateinit var adapter: ManageCulturesAdapter

    // Listas para guardar os resultados das chamadas da API
    private var allCultures: List<Culture>? = null
    private var myCultureIds: Set<Int>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_cultures)

        // 2. Configura o RecyclerView e o Adapter
        setupRecyclerView()

        // 3. Configura os observadores para reagir aos dados do ViewModel
        setupObservers()

        // 4. Inicia a busca dos dados necessários
        viewModel.fetchAllCultures()
        viewModel.fetchUserCultures()
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_manage_cultures)

        // Inicializa o adapter com uma lista vazia e as funções de clique
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
        // Observador para a lista de TODAS as culturas
        viewModel.allCultures.observe(this) { result ->
            result.onSuccess { cultures ->
                this.allCultures = cultures
                combineAndDisplayLists() // Tenta combinar as listas
            }
            result.onFailure { error ->
                Toast.makeText(this, "Erro ao buscar culturas: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }

        // Observador para a lista de culturas DO USUÁRIO
        viewModel.userCultures.observe(this) { result ->
            result.onSuccess { myCultures ->
                // Converte a lista para um Set de IDs para busca rápida
                this.myCultureIds = myCultures.map { it.id }.toSet()
                combineAndDisplayLists() // Tenta combinar as listas
            }
            result.onFailure { error ->
                Toast.makeText(this, "Erro ao buscar suas culturas: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }

        // Observador para o resultado da operação de SALVAR
        viewModel.saveResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Lista de culturas atualizada!", Toast.LENGTH_SHORT).show()
                // Após salvar, busca novamente as culturas do usuário para garantir a sincronia
                viewModel.fetchUserCultures()
            }
            result.onFailure { error ->
                Toast.makeText(this, "Erro ao atualizar: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Esta função é chamada sempre que uma das listas (todas ou do usuário) é carregada.
     * Ela só executa a lógica quando AMBAS as listas estiverem disponíveis.
     */
    private fun combineAndDisplayLists() {
        if (allCultures != null && myCultureIds != null) {
            val combinedList = allCultures!!.map { culture ->
                // Para cada cultura da lista geral, verifica se o ID dela está na lista do usuário
                culture.copy(isSelected = myCultureIds!!.contains(culture.id))
            }
            adapter.updateCultures(combinedList)
        }
    }

    /**
     * Chamado quando o usuário clica em Adicionar ou Remover.
     * Calcula a nova lista de IDs e envia para a API.
     */
    private fun updateUserCultures(changedCulture: Culture, add: Boolean) {
        // Pega os IDs atuais e os converte para uma lista mutável
        val currentIds = myCultureIds?.toMutableSet() ?: mutableSetOf()

        if (add) {
            currentIds.add(changedCulture.id)
        } else {
            currentIds.remove(changedCulture.id)
        }

        // Envia a lista final de IDs para o ViewModel salvar
        viewModel.saveUserCultures(currentIds.toList())
    }
}