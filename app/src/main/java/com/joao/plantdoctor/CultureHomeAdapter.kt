package com.joao.plantdoctor.adapter // ✅ PACOTE CORRIGIDO

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.joao.plantdoctor.R
import com.joao.plantdoctor.activities.CultureDetailsActivity // ✅ IMPORT CORRETO
import com.joao.plantdoctor.models.Culture

class CultureHomeAdapter(
    private var cultures: List<Culture>
) : RecyclerView.Adapter<CultureHomeAdapter.ViewHolder>() {

    // ... (o resto do código do adapter continua igual)

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.iv_culture_home_image)
        val name: TextView = view.findViewById(R.id.tv_culture_home_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_culture_home, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val culture = cultures[position]
        holder.name.text = culture.name
        holder.image.load(culture.imageUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_leaf)
            error(R.drawable.ic_leaf)
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, CultureDetailsActivity::class.java).apply {
                putExtra("CULTURE_ID", culture.id)
                putExtra("CULTURE_NAME", culture.name)
                putExtra("CULTURE_IMAGE_URL", culture.imageUrl)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = cultures.size

    fun updateCultures(newCultures: List<Culture>) {
        this.cultures = newCultures
        notifyDataSetChanged()
    }
}