package com.joao.plantdoctor.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joao.plantdoctor.R
import com.joao.plantdoctor.adapter.CultureHomeAdapter // Reutilizando o adapter de exibição
import com.joao.plantdoctor.viewmodel.CultureViewModel

class CulturesFragment : Fragment() {

    private val viewModel: CultureViewModel by viewModels()
    // Você provavelmente usará o adapter de gerenciamento ou o de home. Usaremos o HomeAdapter como exemplo.
    private lateinit var adapter: CultureHomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla o layout onde o RecyclerView deveria estar (fragment_cultures)
        val view = inflater.inflate(R.layout.fragment_cultures, container, false)

        // Inicializa e configura o RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_cultures_list) // ⚠️ VOCÊ PRECISA CRIAR ESTE ID NO SEU LAYOUT XML!
        setupRecyclerView(recyclerView)
        setupObservers()

        // ✅ INICIA A BUSCA DE DADOS AO CARREGAR O FRAGMENTO
        viewModel.fetchUserCultures()

        return view
    }

    // Configuração básica do RecyclerView
    private fun setupRecyclerView(recyclerView: RecyclerView) {
        adapter = CultureHomeAdapter(emptyList()) // Use o adapter correto para esta tela
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(context, 2) // Exemplo: Layout em grade
    }

    private fun setupObservers() {
        viewModel.userCultures.observe(viewLifecycleOwner) { result ->
            result.onSuccess { cultures ->
                if (cultures.isEmpty()) {
                    adapter.updateCultures(emptyList())
                    // Considere mostrar um texto "Você não tem culturas selecionadas"
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