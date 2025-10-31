package com.joao.PlantSoS.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joao.PlantSoS.R
import com.joao.PlantSoS.adapter.UserHistoryAdapter
import com.joao.PlantSoS.viewmodel.UserHistoryViewModel

class UserHistoryFragment : Fragment(R.layout.fragment_user_history) {

    private val viewModel: UserHistoryViewModel by viewModels()
    private lateinit var adapter: UserHistoryAdapter
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Pega o ID do usuário que foi passado para o fragment
        arguments?.let {
            userId = it.getInt("USER_ID", -1)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_history)
        setupRecyclerView(recyclerView)
        setupObservers()

        if (userId != -1) {
            viewModel.fetchUserHistory(userId)
        } else {
            Toast.makeText(context, "Erro: ID do usuário não encontrado.", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        adapter = UserHistoryAdapter(emptyList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }

    private fun setupObservers() {
        viewModel.historyList.observe(viewLifecycleOwner) { result ->
            result.onSuccess { historyList ->
                adapter.updateHistory(historyList)
            }.onFailure { error ->
                Toast.makeText(context, "Erro ao carregar histórico: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        fun newInstance(userId: Int): UserHistoryFragment {
            val fragment = UserHistoryFragment()
            val args = Bundle()
            args.putInt("USER_ID", userId)
            fragment.arguments = args
            return fragment
        }
    }
}