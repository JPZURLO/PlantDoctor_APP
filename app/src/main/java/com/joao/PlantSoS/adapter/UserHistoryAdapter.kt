package com.joao.PlantSoS.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.joao.PlantSoS.R
import com.joao.PlantSoS.models.UserHistoryItem
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class UserHistoryAdapter(private var historyItems: List<UserHistoryItem>) : RecyclerView.Adapter<UserHistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val description: TextView = view.findViewById(R.id.tv_history_description)
        val details: TextView = view.findViewById(R.id.tv_history_details)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = historyItems[position]

        holder.description.text = "O campo '${item.fieldChanged}' foi alterado de '${item.oldValue}' para '${item.newValue}'."

        // Formata a data para um formato mais legível
        val formattedDate = try {
            val odt = OffsetDateTime.parse(item.changedAt)
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            odt.format(formatter)
        } catch (e: Exception) {
            item.changedAt // Usa a data original se a formatação falhar
        }

        holder.details.text = "Alterado por: ${item.editorName} em $formattedDate"
    }

    override fun getItemCount() = historyItems.size

    fun updateHistory(newHistory: List<UserHistoryItem>) {
        this.historyItems = newHistory
        notifyDataSetChanged()
    }
}