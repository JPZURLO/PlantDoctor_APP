package com.joao.plantdoctor.fragments

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
import com.joao.plantdoctor.R
import com.joao.plantdoctor.adapter.DoubtsAdapter
import com.joao.plantdoctor.viewmodel.DoubtsViewModel

class DoubtsFragment : Fragment() {

    private val viewModel: DoubtsViewModel by viewModels()
    private lateinit var adapter: DoubtsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_doubts, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_doubts)
        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fab_add_doubt)
        val etNewDoubt = view.findViewById<EditText>(R.id.et_new_doubt)

        setupRecyclerView(recyclerView)
        setupObservers()

        fabAdd.setOnClickListener {
            val newQuestion = etNewDoubt.text.toString()
            if (newQuestion.isNotEmpty()) {
                viewModel.postDoubt(newQuestion)
                etNewDoubt.text.clear()
            } else {
                Toast.makeText(context, "Escreva sua dúvida primeiro.", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        // Busca as dúvidas sempre que a tela se torna visível
        viewModel.fetchDoubts()
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        // Inicializa o adapter com uma lista vazia
        adapter = DoubtsAdapter(emptyList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(context, 3)
    }

    private fun setupObservers() {
        // Observa a lista de dúvidas vinda do ViewModel
        viewModel.doubtsList.observe(viewLifecycleOwner) { result ->
            result.onSuccess { doubts ->
                adapter.updateDoubts(doubts)
            }
            result.onFailure { error ->
                Toast.makeText(context, "Erro ao carregar dúvidas: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }

        // Observa o resultado do envio de uma nova dúvida
        viewModel.postResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(context, "Dúvida enviada com sucesso!", Toast.LENGTH_SHORT).show()
            }
            result.onFailure { error ->
                Toast.makeText(context, "Falha ao enviar dúvida: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}