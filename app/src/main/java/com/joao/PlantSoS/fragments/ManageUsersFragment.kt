package com.joao.PlantSoS.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joao.PlantSoS.R
import com.joao.PlantSoS.activities.EditUserActivity
import com.joao.PlantSoS.adapter.UserAdapter
import com.joao.PlantSoS.viewmodel.ManageUsersViewModel

class ManageUsersFragment : Fragment() {

    private val viewModel: ManageUsersViewModel by viewModels()
    private lateinit var adapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_manage_users, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_users)

        setupRecyclerView(recyclerView)
        setupObservers()

        return view
    }

    override fun onResume() {
        super.onResume()
        // Pede para o ViewModel buscar os usuários sempre que a tela fica visível
        viewModel.fetchAllUsers()
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        // Inicializa o adapter com uma lista vazia e a ação de clique
        adapter = UserAdapter(emptyList()) { user ->
            // AÇÃO DE CLIQUE: Abrir a EditUserActivity, passando o objeto User
            val intent = Intent(requireContext(), EditUserActivity::class.java).apply {
                putExtra("USER_EXTRA", user) // "USER_EXTRA" é a chave para buscar o dado na outra tela
            }
            startActivity(intent)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        // Adiciona uma linha divisória entre os itens da lista
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }

    private fun setupObservers() {
        viewModel.usersList.observe(viewLifecycleOwner) { result ->
            result.onSuccess { users ->
                // Quando a lista de usuários chega da API, atualiza o adapter
                adapter.updateUsers(users)
            }.onFailure { error ->
                Toast.makeText(context, "Erro ao carregar usuários: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}