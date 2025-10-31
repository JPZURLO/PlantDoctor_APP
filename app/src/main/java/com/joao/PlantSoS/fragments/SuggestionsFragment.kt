package com.joao.PlantSoS.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.joao.PlantSoS.R
import com.joao.PlantSoS.adapter.SuggestionsAdapter
import com.joao.PlantSoS.viewmodel.SuggestionsViewModel

class SuggestionsFragment : Fragment() {

    private val viewModel: SuggestionsViewModel by viewModels()
    private lateinit var adapter: SuggestionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_suggestions, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_suggestions)
        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fab_add_suggestion)
        val etNewSuggestion = view.findViewById<EditText>(R.id.et_new_suggestion)

        setupRecyclerView(recyclerView)
        setupObservers()

        fabAdd.setOnClickListener {
            val newSuggestion = etNewSuggestion.text.toString()
            if (newSuggestion.isNotEmpty()) {
                viewModel.postSuggestion(newSuggestion)
                etNewSuggestion.text.clear()
            } else {
                Toast.makeText(context, "Escreva sua sugestão primeiro.", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchSuggestions()
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        adapter = SuggestionsAdapter(emptyList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(context, 2) // Mostra em 2 colunas
    }

    private fun setupObservers() {
        viewModel.suggestionsList.observe(viewLifecycleOwner) { result ->
            result.onSuccess { suggestions ->
                adapter.updateSuggestions(suggestions)
            }.onFailure { error ->
                Toast.makeText(context, "Erro ao carregar sugestões: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.postResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(context, "Sugestão enviada com sucesso!", Toast.LENGTH_SHORT).show()
            }.onFailure { error ->
                Toast.makeText(context, "Falha ao enviar sugestão: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}

