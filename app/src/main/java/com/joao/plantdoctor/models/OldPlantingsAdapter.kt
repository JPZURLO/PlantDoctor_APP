package com.joao.plantdoctor.models // Pacote de modelos ou adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.joao.plantdoctor.R
import java.text.SimpleDateFormat
import java.util.*

// ----------------------------------------------------------------------------------
// 1. CLASSE ADAPTER PRINCIPAL
// ----------------------------------------------------------------------------------

// Assume que PlantedCulture é o modelo que contém o nome da cultura e a data de plantio
class OldPlantingsAdapter : ListAdapter<PlantedCulture, OldPlantingsAdapter.ViewHolder>(PlantedCultureDiffCallback()) {

    // Formato de data da API (ISO 8601: "yyyy-MM-dd")
    private val apiSdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    // Formato de data para exibição na UI
    private val displaySdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // IDs que você deve ter no seu item_old_planting.xml
        val nameTextView: TextView = view.findViewById(R.id.tv_old_planting_name)
        val dateTextView: TextView = view.findViewById(R.id.tv_old_planting_date)
        val cycleTextView: TextView = view.findViewById(R.id.tv_old_planting_cycle)

        // Formato para exibição dentro do ViewHolder
        private val apiSdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        private val displaySdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        fun bind(plantedCulture: PlantedCulture) {

            // 1. Nome e ID
            nameTextView.text = plantedCulture.culture.name

            // 2. Data
            val dateString = try {
                val date = apiSdf.parse(plantedCulture.planting_date)
                displaySdf.format(date)
            } catch (e: Exception) {
                "Data Inválida"
            }
            dateTextView.text = "Plantio: $dateString"

            // 3. Notas (se houver, ou outro campo que identifique o ciclo)
            cycleTextView.text = plantedCulture.notes ?: "Plantio #${plantedCulture.id}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_old_planting, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

// ----------------------------------------------------------------------------------
// 2. CLASSE DE CALLBACK (Para o ListAdapter)
// ----------------------------------------------------------------------------------

// Classe auxiliar para otimizar o ListAdapter
class PlantedCultureDiffCallback : DiffUtil.ItemCallback<PlantedCulture>() {
    override fun areItemsTheSame(oldItem: PlantedCulture, newItem: PlantedCulture): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PlantedCulture, newItem: PlantedCulture): Boolean {
        // Uma simples verificação de igualdade de dados
        return oldItem == newItem
    }
}