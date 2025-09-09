package com.joao.plantdoctor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.joao.plantdoctor.models.Culture

class CultureHomeAdapter(
    // ✅ Alterado para 'var' para que a lista possa ser atualizada
    private var cultures: List<Culture>
) : RecyclerView.Adapter<CultureHomeAdapter.ViewHolder>() {

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
        // Usa a biblioteca Coil para carregar a imagem a partir da URL
        holder.image.load(culture.imageUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_leaf) // Imagem de placeholder enquanto carrega
            error(R.drawable.ic_leaf)       // Imagem a ser mostrada em caso de erro
        }
    }

    override fun getItemCount() = cultures.size

    // ✅✅✅ NOVO MÉTODO ADICIONADO AQUI ✅✅✅
    /**
     * Atualiza a lista de culturas no adapter e notifica o RecyclerView para
     * que ele se redesenhe com os novos dados.
     */
    fun updateCultures(newCultures: List<Culture>) {
        this.cultures = newCultures
        notifyDataSetChanged() // Informa ao adapter que os dados mudaram
    }
}

