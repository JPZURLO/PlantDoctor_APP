package com.joao.plantdoctor.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager // Import adicionado
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.joao.plantdoctor.R
import com.joao.plantdoctor.adapter.CultureHomeAdapter
import com.joao.plantdoctor.viewmodel.CultureViewModel

class MyCulturesFragment : Fragment() {

    private val viewModel: CultureViewModel by viewModels()
    private lateinit var adapter: CultureHomeAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_cultures, container, false)

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_cultures)

        setupRecyclerView(recyclerView)
        setupObservers()
        setupPullToRefresh()

        return view
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchUserCultures()
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        adapter = CultureHomeAdapter(emptyList())
        recyclerView.adapter = adapter
        // ALTERADO AQUI para exibir em 2 colunas
        recyclerView.layoutManager = GridLayoutManager(context, 2)
    }

    private fun setupPullToRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchUserCultures()
        }
    }

    private fun setupObservers() {
        viewModel.userCultures.observe(viewLifecycleOwner) { result ->
            swipeRefreshLayout.isRefreshing = false
            result.onSuccess { cultures ->
                adapter.updateCultures(cultures.orEmpty())
            }
            result.onFailure { error ->
                // ▼▼▼ ADICIONE ESTA LINHA PARA VER O ERRO NO LOGCAT ▼▼▼
                Log.e("MyCulturesFragment", "Falha ao buscar culturas: ", error)

                Toast.makeText(context, "Erro: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}