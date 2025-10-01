// No arquivo HistoryAdapter.kt

package com.joao.plantdoctor.models

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.joao.plantdoctor.R
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter : ListAdapter<AtividadeHistorico, HistoryAdapter.HistoryViewHolder>(HistoryDiffCallback()) {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventTypeTextView: TextView = itemView.findViewById(R.id.tv_event_type)
        private val eventDateTextView: TextView = itemView.findViewById(R.id.tv_event_date)
        private val eventObservationTextView: TextView = itemView.findViewById(R.id.tv_event_observation)

        // Função 'bind' atualizada com os novos nomes de campos
        fun bind(item: AtividadeHistorico) {
            // Usa o eventType para o título principal
            eventTypeTextView.text = item.eventType.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

            // Usa a 'observation' para o campo de observação
            eventObservationTextView.text = item.observation
            eventObservationTextView.visibility = View.VISIBLE

            // Formata a data para um formato mais legível
            eventDateTextView.text = formatDisplayDate(item.eventDate)
        }

        // Função para formatar a data que vem da API
        private fun formatDisplayDate(isoDate: String): String {
            return try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val formatter = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.getDefault())
                formatter.format(parser.parse(isoDate)!!)
            } catch (e: Exception) {
                isoDate // Em caso de erro, mostra a data original
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
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