package com.joao.plantdoctor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.joao.plantdoctor.models.Culture

class CultureAdapter(private val cultures: List<Culture>) :
    RecyclerView.Adapter<CultureAdapter.CultureViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CultureViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_culture, parent, false)
        return CultureViewHolder(view)
    }

    override fun onBindViewHolder(holder: CultureViewHolder, position: Int) {
        val culture = cultures[position]
        holder.bind(culture)
    }

    override fun getItemCount() = cultures.size

    fun getSelectedCultures(): List<Culture> {
        return cultures.filter { it.isSelected }
    }

    inner class CultureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val containerLayout: LinearLayout = itemView.findViewById(R.id.container_layout)
        private val ivCulture: ImageView = itemView.findViewById(R.id.iv_culture_image)
        private val tvCulture: TextView = itemView.findViewById(R.id.tv_culture_name)

        fun bind(culture: Culture) {
            ivCulture.setImageResource(culture.imageResId)
            tvCulture.text = culture.name
            containerLayout.isSelected = culture.isSelected

            itemView.setOnClickListener {
                culture.isSelected = !culture.isSelected
                containerLayout.isSelected = culture.isSelected
            }
        }
    }
}

