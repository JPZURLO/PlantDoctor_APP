package com.joao.plantdoctor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment() {

    // Usa o HomeViewModel que já tínhamos criado antes
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: CultureHomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla o layout para este fragmento
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_home_cultures)
        setupRecyclerView(recyclerView)
        setupObservers()
    }

    // Busca as culturas sempre que o fragmento é resumido, para garantir dados atualizados
    override fun onResume() {
        super.onResume()
        viewModel.fetchMyCultures()
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        adapter = CultureHomeAdapter(emptyList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun setupObservers() {
        viewModel.myCultures.observe(viewLifecycleOwner) { result ->
            result.onSuccess { cultures ->
                adapter.updateCultures(cultures)
                if (cultures.isEmpty()) {
                    Toast.makeText(context, "Você ainda não adicionou culturas.", Toast.LENGTH_SHORT).show()
                }
            }
            result.onFailure { error ->
                Toast.makeText(context, "Erro ao buscar suas culturas: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}