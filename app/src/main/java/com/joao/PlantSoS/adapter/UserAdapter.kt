package com.joao.PlantSoS.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.joao.PlantSoS.R
import com.joao.PlantSoS.models.User

class UserAdapter(
    private var users: List<User>,
    private val onItemClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    /**
     * ViewHolder que contém as referências para as Views de cada item da lista.
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_user_name)
        val email: TextView = view.findViewById(R.id.tv_user_email)
        val role: TextView = view.findViewById(R.id.tv_user_role)
    }

    /**
     * Chamado quando o RecyclerView precisa de um novo ViewHolder (um novo item visual).
     * Ele infla o layout do item (item_user.xml).
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    /**
     * Chamado para conectar os dados de um usuário específico (da lista) com o ViewHolder (o visual).
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.name.text = user.name
        holder.email.text = user.email
        holder.role.text = user.userType

        // Configura o clique no item inteiro
        holder.itemView.setOnClickListener {
            onItemClick(user)
        }
    }

    /**
     * Retorna o número total de itens na lista.
     */
    override fun getItemCount() = users.size

    /**
     * Função para atualizar a lista de usuários no adapter e notificar o RecyclerView.
     */
    fun updateUsers(newUsers: List<User>) {
        this.users = newUsers
        notifyDataSetChanged()
    }
}