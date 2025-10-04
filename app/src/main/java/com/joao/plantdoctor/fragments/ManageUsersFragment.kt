package com.joao.plantdoctor.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.joao.plantdoctor.R
import com.joao.plantdoctor.adapter.UserAdapter
import com.joao.plantdoctor.viewmodel.ManageUsersViewModel

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
        viewModel.fetchAllUsers()
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        adapter = UserAdapter(emptyList()) { user ->
            // TODO: Navegar para a tela de edição de usuário, passando o user.id
            Toast.makeText(context, "Clicou em: ${user.name}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.usersList.observe(viewLifecycleOwner) { result ->
            result.onSuccess { users ->
                adapter.updateUsers(users)
            }.onFailure { error ->
                Toast.makeText(context, "Erro: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}