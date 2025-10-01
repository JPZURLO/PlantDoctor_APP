package com.joao.plantdoctor.activities

import ManageCulturesAdapter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joao.plantdoctor.R
import com.joao.plantdoctor.models.Culture
// ✅✅✅ ADICIONE ESTE IMPORT QUE ESTAVA FALTANDO ✅✅✅
import com.joao.plantdoctor.viewmodel.CultureViewModel

class ManageCulturesActivity : AppCompatActivity() {

    // Esta linha agora vai funcionar
    private val viewModel: CultureViewModel by viewModels()
    private lateinit var adapter: ManageCulturesAdapter

    private var allCultures: List<Culture>? = null
    private var myCultureIds: Set<Int>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_cultures)

        setupRecyclerView()
        setupObservers()

        viewModel.fetchAllCultures()
        viewModel.fetchUserCultures()
    }

    private fun setupRecyclerView() {
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
        viewModel.allCultures.observe(this) { result ->
            result.onSuccess { cultures ->
                this.allCultures = cultures
                combineAndDisplayLists()
            }
            result.onFailure { error ->
                Toast.makeText(this, "Erro ao buscar culturas: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.userCultures.observe(this) { result ->
            result.onSuccess { myCultures ->
                this.myCultureIds = myCultures.map { it.id }.toSet()
                combineAndDisplayLists()
            }
            result.onFailure { error ->
                Toast.makeText(this, "Erro ao buscar suas culturas: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.saveResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Lista de culturas atualizada!", Toast.LENGTH_SHORT).show()
                viewModel.fetchUserCultures()
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

    private fun updateUserCultures(changedCulture: Culture, add: Boolean) {
        val currentIds = myCultureIds?.toMutableSet() ?: mutableSetOf()

        if (add) {
            currentIds.add(changedCulture.id)
        } else {
            currentIds.remove(changedCulture.id)
        }

        viewModel.saveUserCultures(currentIds.toList())
    }
}