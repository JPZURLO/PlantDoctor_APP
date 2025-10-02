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
import java.util.*

// Lembre-se de importar PlantedCulture para que o ListAdapter funcione.
// import com.joao.plantdoctor.models.PlantedCulture
// (Assumindo que PlantedCulture está em com.joao.plantdoctor.models)

// ----------------------------------------------------------------------------------
// 1. CLASSE ADAPTER PRINCIPAL (Corrigida)
// ----------------------------------------------------------------------------------

class OldPlantingsAdapter(
    // ✅ CORREÇÃO: Adiciona a função de clique ao construtor
    private val onItemClick: (PlantedCulture) -> Unit
) : ListAdapter<PlantedCulture, OldPlantingsAdapter.ViewHolder>(PlantedCultureDiffCallback()) {

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
            // Nota: Se 'culture' não for resolvido, verifique o modelo PlantedCulture
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
        val item = getItem(position)
        holder.bind(item)

        // ✅ CORREÇÃO: Adiciona o listener de clique que executa a função da Activity
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }
}

// ----------------------------------------------------------------------------------
// 2. CLASSE DE CALLBACK (Inalterada)
// ----------------------------------------------------------------------------------

class PlantedCultureDiffCallback : DiffUtil.ItemCallback<PlantedCulture>() {
    override fun areItemsTheSame(oldItem: PlantedCulture, newItem: PlantedCulture): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PlantedCulture, newItem: PlantedCulture): Boolean {
        return oldItem == newItem
    }
}