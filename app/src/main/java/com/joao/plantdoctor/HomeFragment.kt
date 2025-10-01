package com.joao.plantdoctor.fragments // ✅ PACOTE CORRETO

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joao.plantdoctor.R
import com.joao.plantdoctor.adapter.CultureHomeAdapter // ✅ IMPORT CORRETO
import com.joao.plantdoctor.viewmodel.CultureViewModel

class HomeFragment : Fragment() {

    private val viewModel: CultureViewModel by viewModels()
    private lateinit var adapter: CultureHomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_home)
        setupRecyclerView(recyclerView)
        setupObservers()

        viewModel.fetchUserCultures()

        return view
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        adapter = CultureHomeAdapter(emptyList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setupObservers() {
        viewModel.userCultures.observe(viewLifecycleOwner) { result ->
            result.onSuccess { cultures ->
                if (cultures.isEmpty()) {
                    adapter.updateCultures(emptyList())
                    // Considere mostrar uma mensagem na tela em vez de um Toast
                } else {
                    adapter.updateCultures(cultures)
                }
            }
            result.onFailure { error ->
                Toast.makeText(context, "Erro ao buscar suas culturas: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}