package com.joao.plantdoctor.models // ou o pacote onde ele está

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.joao.plantdoctor.R

// A estrutura do Adapter continua a mesma
class HistoryAdapter : ListAdapter<AtividadeHistorico, HistoryAdapter.HistoryViewHolder>(HistoryDiffCallback()) {

    // ▼▼▼ A MUDANÇA PRINCIPAL ACONTECE AQUI DENTRO ▼▼▼
    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // 1. As variáveis agora procuram pelos NOVOS IDs do seu XML
        private val eventTypeTextView: TextView = itemView.findViewById(R.id.tv_event_type)
        private val eventDateTextView: TextView = itemView.findViewById(R.id.tv_event_date)
        private val eventObservationTextView: TextView = itemView.findViewById(R.id.tv_event_observation)

        // 2. A função 'bind' agora preenche os novos TextViews
        fun bind(item: AtividadeHistorico) {
            // Mapeamento:
            // A 'descricao' da atividade vai para o campo principal (em negrito)
            eventTypeTextView.text = item.descricao
            // A 'data' da atividade vai para o campo de data
            eventDateTextView.text = item.data

            // O campo 'observation' por enquanto ficará escondido se não houver texto
            // No futuro, você pode adicionar um campo "observacao" na sua classe AtividadeHistorico
            eventObservationTextView.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        // Garanta que o nome do layout aqui é o mesmo do seu arquivo
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_event, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

// O DiffUtil continua igual
class HistoryDiffCallback : DiffUtil.ItemCallback<AtividadeHistorico>() {
    override fun areItemsTheSame(oldItem: AtividadeHistorico, newItem: AtividadeHistorico): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: AtividadeHistorico, newItem: AtividadeHistorico): Boolean {
        return oldItem == newItem
    }
}