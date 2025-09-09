package com.joao.plantdoctor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.joao.plantdoctor.models.Culture

class CultureAdapter(
    // ✅ Alterado para 'var' para que a lista possa ser atualizada
    private var cultures: List<Culture>
) : RecyclerView.Adapter<CultureAdapter.CultureViewHolder>() {

    // ✅✅✅ NOVO MÉTODO ADICIONADO AQUI ✅✅✅
    /**
     * Atualiza a lista de culturas no adapter e notifica o RecyclerView para
     * que ele se redesenhe com os novos dados.
     */
    fun updateCultures(newCultures: List<Culture>) {
        this.cultures = newCultures
        notifyDataSetChanged() // Informa ao adapter que os dados mudaram
    }

    fun getSelectedCultures(): List<Culture> {
        return cultures.filter { it.isSelected }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CultureViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_culture, parent, false)
        return CultureViewHolder(view)
    }

    override fun onBindViewHolder(holder: CultureViewHolder, position: Int) {
        holder.bind(cultures[position])
    }

    override fun getItemCount() = cultures.size

    inner class CultureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val containerLayout: LinearLayout = itemView.findViewById(R.id.container_layout)
        private val ivCulture: ImageView = itemView.findViewById(R.id.iv_culture_image)
        private val tvCulture: TextView = itemView.findViewById(R.id.tv_culture_name)

        fun bind(culture: Culture) {
            tvCulture.text = culture.name
            containerLayout.isSelected = culture.isSelected
            // Usa a biblioteca Coil para carregar a imagem a partir da URL
            ivCulture.load(culture.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_leaf)
                error(R.drawable.ic_leaf)
            }

            itemView.setOnClickListener {
                culture.isSelected = !culture.isSelected
                containerLayout.isSelected = culture.isSelected
            }
        }
    }
}

