package com.joao.plantdoctor

import android.content.Intent
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
        // ✅✅✅ NOVA LÓGICA DE CLIQUE ADICIONADA AQUI ✅✅✅
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            // NOTA: A Activity 'CultureDetailsActivity' ainda não foi criada.
            // Este é o próximo passo.
            val intent = Intent(context, CultureDetailsActivity::class.java).apply {
                // Passa o ID e o nome da cultura para a próxima tela
                putExtra("CULTURE_ID", culture.id)
                putExtra("CULTURE_NAME", culture.name)
                putExtra("CULTURE_IMAGE_URL", culture.imageUrl)
            }
            context.startActivity(intent)
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

