package com.joao.plantdoctor.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.joao.plantdoctor.R
import com.joao.plantdoctor.adapter.CultureHomeAdapter
import com.joao.plantdoctor.viewmodel.CultureViewModel

class HomeFragment : Fragment() {

    private val viewModel: CultureViewModel by viewModels()
    private lateinit var adapter: CultureHomeAdapter

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Inicializa o SwipeRefreshLayout e o RecyclerView
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_home)

        // ✅ CORREÇÃO: Chama os métodos que agora estarão presentes
        setupRecyclerView(recyclerView)
        setupObservers()
        setupPullToRefresh()

        return view
    }

    override fun onResume() {
        super.onResume()
        // Garante a atualização na volta para a tela
        viewModel.fetchUserCultures()
    }

    // ----------------------------------------------------
    // ✅ MÉTODO FALTANTE (SOLUÇÃO DO SEU ERRO)
    // ----------------------------------------------------
    private fun setupRecyclerView(recyclerView: RecyclerView) {
        adapter = CultureHomeAdapter(emptyList())
        recyclerView.adapter = adapter
        // Para a lista horizontal
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }
    // ----------------------------------------------------

    private fun setupPullToRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            // Inicia a busca de dados
            viewModel.fetchUserCultures()
        }
    }

    private fun setupObservers() {
        viewModel.userCultures.observe(viewLifecycleOwner) { result ->
            // Para o indicador de loading do Pull-to-Refresh após a busca
            swipeRefreshLayout.isRefreshing = false

            result.onSuccess { cultures ->
                if (cultures.isEmpty()) {
                    adapter.updateCultures(emptyList())
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