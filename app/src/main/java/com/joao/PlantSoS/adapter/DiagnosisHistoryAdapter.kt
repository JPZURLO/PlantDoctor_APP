package com.joao.PlantSoS.adapter

import android.content.Intent // 1. ADICIONE O IMPORT
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
import com.joao.PlantSoS.activities.DiseaseExplanationActivity // 2. ADICIONE O IMPORT DA TELA
import com.joao.PlantSoS.models.DiagnosisHistoryItem
import java.text.SimpleDateFormat
import java.util.Locale

class DiagnosisHistoryAdapter :
    ListAdapter<DiagnosisHistoryItem, DiagnosisHistoryAdapter.DiagnosisViewHolder>(DiagnosisDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiagnosisViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_diagnosis_history, parent, false)
        return DiagnosisViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiagnosisViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // 3. O "ViewHolder" (a classe interna)
    inner class DiagnosisViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivThumbnail: ImageView = itemView.findViewById(R.id.iv_diagnosis_thumbnail)
        private val txtName: TextView = itemView.findViewById(R.id.txtDiagnosisName)
        private val txtDate: TextView = itemView.findViewById(R.id.txtDiagnosisDate)
        private val txtObs: TextView = itemView.findViewById(R.id.txtObservation)

        // 4. Crie uma variável para guardar o item atual
        private var currentItem: DiagnosisHistoryItem? = null

        // 5. Adicione o 'init' para configurar o clique
        init {
            itemView.setOnClickListener {
                currentItem?.let { item ->
                    // 6. Pega o contexto (a tela)
                    val context = itemView.context
                    // 7. Cria a "carta" (Intent) para a nova tela
                    val intent = Intent(context, DiseaseExplanationActivity::class.java).apply {
                        // 8. Coloca o nome da doença na "carta"
                        putExtra("DISEASE_NAME", item.diagnosis_name)
                    }
                    // 9. Envia (abre a tela)
                    context.startActivity(intent)
                }
            }
        }

        fun bind(item: DiagnosisHistoryItem) {
            // 10. Salva o item para o clique funcionar
            this.currentItem = item

            txtName.text = item.diagnosis_name.replace("_", " ") // Bônus: troca _ por espaço
            txtObs.text = item.observation ?: "Sem observações"

            // 🔹 Formatar data
            try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val formatted = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val finalDate = formatted.format(parser.parse(item.analysis_date)!!)
                txtDate.text = finalDate
            } catch (e: Exception) {
                txtDate.text = "Data inválida"
            }

            // 🔹 Carregar imagem
            ivThumbnail.load(item.photo_path) {
                crossfade(true)
                placeholder(R.drawable.ic_leaf)
                error(R.drawable.ic_leaf2)
            }
        }
    }

    // O DiffCallback (sem alteração)
    class DiagnosisDiffCallback : DiffUtil.ItemCallback<DiagnosisHistoryItem>() {
        override fun areItemsTheSame(oldItem: DiagnosisHistoryItem, newItem: DiagnosisHistoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DiagnosisHistoryItem, newItem: DiagnosisHistoryItem): Boolean {
            return oldItem == newItem
        }
    }
}