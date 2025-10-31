package com.joao.PlantSoS.fragments

import android.os.Bundle
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
// ✅ IMPORTS ADICIONADOS
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.joao.PlantSoS.activities.ManageCulturesActivity
// ---
import com.joao.PlantSoS.R
import com.joao.PlantSoS.adapter.CultureHomeAdapter
import com.joao.PlantSoS.activities.CultureDetailsActivity
import com.joao.PlantSoS.viewmodel.CultureViewModel

// (Este era o seu MyCulturesFragment)
class CulturesFragment : Fragment() {

    private val viewModel: CultureViewModel by viewModels()
    private lateinit var adapter: CultureHomeAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    // ✅ ADICIONADO: Referência para o novo botão "Editar"
    private lateinit var fabEditCultures: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla o layout que você acabou de editar (fragment_my_cultures.xml)
        val view = inflater.inflate(R.layout.fragment_my_cultures, container, false)

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_cultures)

        // ✅ ADICIONADO: Encontra o FAB pelo ID do XML
        fabEditCultures = view.findViewById(R.id.fab_edit_cultures)

        setupRecyclerView(recyclerView)
        setupObservers()
        setupPullToRefresh()

        // ✅ ADICIONADO: Configura o clique do FAB
        fabEditCultures.setOnClickListener {
            // Abre a tela de Gestão de Culturas
            val intent = Intent(requireContext(), ManageCulturesActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        // Esta linha é PERFEITA.
        // Quando o utilizador voltar da ManageCulturesActivity, isto
        // vai atualizar a lista de culturas automaticamente.
        viewModel.fetchUserCultures()
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        adapter = CultureHomeAdapter(emptyList()) { selectedCulture ->
            // Ação de clique no item (ISTO ESTÁ CORRETO): Abrir Detalhes
            val context = requireContext()
            val intent = Intent(context, CultureDetailsActivity::class.java).apply {
                putExtra("CULTURE_ID", selectedCulture.id)
                putExtra("CULTURE_NAME", selectedCulture.name)
                putExtra("CULTURE_IMAGE_URL", selectedCulture.imageUrl)
            }
            context.startActivity(intent)
        }

        recyclerView.adapter = adapter
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
                Log.e("CulturesFragment", "Falha ao buscar culturas: ", error)
                Toast.makeText(context, "Erro: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}

