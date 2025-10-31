package com.joao.PlantSoS.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.joao.PlantSoS.R
import com.joao.PlantSoS.models.DiagnosisHistoryItem
import java.text.SimpleDateFormat
import java.util.Locale

// Usamos ListAdapter para melhor performance (ele calcula as diferenças)
class DiagnosisHistoryAdapter : ListAdapter<DiagnosisHistoryItem, DiagnosisHistoryAdapter.ViewHolder>(DiagnosisDiffCallback()) {

    // Formato de data/hora para exibir no item
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())

    /**
     * O ViewHolder encontra os 'Views' (componentes) dentro do layout do item
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val thumbnail: ImageView = view.findViewById(R.id.iv_diagnosis_thumbnail)
        val diagnosisName: TextView = view.findViewById(R.id.tv_diagnosis_name)
        val cultureName: TextView = view.findViewById(R.id.tv_diagnosis_culture_name)
        val dateTime: TextView = view.findViewById(R.id.tv_diagnosis_datetime)
        val observations: TextView = view.findViewById(R.id.tv_diagnosis_observations)
    }

    /**
     * Infla (cria) o layout 'item_diagnosis_history.xml' para cada item
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_diagnosis_history, parent, false)
        return ViewHolder(view)
    }

    /**
     * Conecta os dados do item (ex: nome do diagnóstico) aos componentes visuais
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.diagnosisName.text = item.diagnosisName
        holder.cultureName.text = "Cultura: ${item.cultureName}" // Adiciona um contexto
        holder.observations.text = item.observation
        holder.dateTime.text = dateFormat.format(item.analysisDate)

        // Usa o Coil (que você já tem) para carregar a imagem da URI
        holder.thumbnail.load(item.photoPath) {
            crossfade(true)
            placeholder(R.drawable.ic_leaf) // Seu placeholder
            error(R.drawable.ic_leaf)       // Seu ícone de erro
        }
    }
}

/**
 * Classe de 'DiffUtil' para o ListAdapter saber o que mudou na lista
 */
class DiagnosisDiffCallback : DiffUtil.ItemCallback<DiagnosisHistoryItem>() {
    override fun areItemsTheSame(oldItem: DiagnosisHistoryItem, newItem: DiagnosisHistoryItem): Boolean {
        return oldItem.id == newItem.id // Compara pelo ID único
    }

    override fun areContentsTheSame(oldItem: DiagnosisHistoryItem, newItem: DiagnosisHistoryItem): Boolean {
        return oldItem == newItem // O data class compara todos os campos
    }
}