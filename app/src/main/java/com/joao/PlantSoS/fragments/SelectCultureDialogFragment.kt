package com.joao.PlantSoS.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joao.PlantSoS.R
import com.joao.PlantSoS.adapter.CultureHomeAdapter // TODO: Verifique se este adapter existe
import com.joao.PlantSoS.models.Culture
import com.joao.PlantSoS.viewmodel.CultureViewModel

// Interface para comunicar de volta com o DiagnoseFragment
interface CultureSelectListener {
    fun onCultureSelected(culture: Culture)
}

class SelectCultureDialogFragment : DialogFragment() {

    private val viewModel: CultureViewModel by viewModels()
    private lateinit var adapter: CultureHomeAdapter

    // O 'listener' será o DiagnoseFragment
    var listener: CultureSelectListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla o layout que criamos para o modal
        val view = inflater.inflate(R.layout.dialog_select_culture, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_select_culture)

        setupRecyclerView(recyclerView)
        setupObservers()

        viewModel.fetchUserCultures()
        return view
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        // TODO: ADAPTE SEU 'CultureHomeAdapter' para aceitar um listener de clique
        adapter = CultureHomeAdapter(emptyList()) { selectedCulture ->
            // QUANDO CLICAR EM UMA CULTURA:
            // Avisa o listener (DiagnoseFragment) e fecha o modal
            listener?.onCultureSelected(selectedCulture)
            dismiss() // Fecha o modal
        }

        recyclerView.adapter = adapter

        // Exibindo em 2 colunas, como você sugeriu
        recyclerView.layoutManager = GridLayoutManager(context, 2)
    }

    private fun setupObservers() {
        // Lógica de busca de culturas, igual ao seu MyCulturesFragment
        viewModel.userCultures.observe(viewLifecycleOwner) { result ->
            result.onSuccess { cultures ->
                adapter.updateCultures(cultures.orEmpty())
            }
            result.onFailure {
                Log.e("SelectCultureDialog", "Erro ao buscar culturas", it)
                Toast.makeText(context, "Erro ao buscar culturas", Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }
    }

    companion object {
        fun newInstance(): SelectCultureDialogFragment {
            return SelectCultureDialogFragment()
        }
    }
}