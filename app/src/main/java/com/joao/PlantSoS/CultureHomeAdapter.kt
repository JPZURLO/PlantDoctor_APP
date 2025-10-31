package com.joao.PlantSoS.adapter

import android.annotation.SuppressLint
import android.content.Intent // Vamos mover isso para o Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.joao.PlantSoS.R
import com.joao.PlantSoS.activities.CultureDetailsActivity
import com.joao.PlantSoS.models.Culture

// ✅ MUDANÇA 1: Adicione um 'listener' de clique no construtor
class CultureHomeAdapter(
    private var cultures: List<Culture>,
    private val onItemClick: (Culture) -> Unit  // <-- Esta é a função que será chamada no clique
) : RecyclerView.Adapter<CultureHomeAdapter.ViewHolder>() {

    // Seu ViewHolder está perfeito, sem mudanças
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.iv_culture_home_image)
        val name: TextView = view.findViewById(R.id.tv_culture_home_name)
    }

    // Seu onCreateViewHolder está perfeito, sem mudanças
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_culture_home, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("LongLogTag")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("CultureHomeAdapter_Debug", "onBindViewHolder chamado para a posição: $position")

        val culture = cultures[position]
        holder.name.text = culture.name // Use 'culture.name' (do seu adapter original)
        holder.image.load(culture.imageUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_leaf)
            error(R.drawable.ic_leaf)
        }

        // ✅ MUDANÇA 2: O clique agora chama o 'listener'
        holder.itemView.setOnClickListener {
            onItemClick(culture) // Simplesmente avisa quem criou o adapter
        }
    }

    // Seu getItemCount está perfeito
    @SuppressLint("LongLogTag")
    override fun getItemCount(): Int {
        val count = cultures.size
        Log.d("CultureHomeAdapter_Debug", "getItemCount chamado. Contagem: $count")
        return count
    }

    // Seu updateCultures está perfeito
    @SuppressLint("LongLogTag")
    fun updateCultures(newCultures: List<Culture>) {
        Log.d("CultureHomeAdapter_Debug", "updateCultures chamado com ${newCultures.size} culturas.")
        this.cultures = newCultures
        notifyDataSetChanged()
    }
}