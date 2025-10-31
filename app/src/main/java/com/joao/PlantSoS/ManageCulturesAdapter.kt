// No topo do arquivo ManageCulturesAdapter.kt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.joao.PlantSoS.R
import com.joao.PlantSoS.models.Culture
class ManageCulturesAdapter(
    private var cultures: List<Culture>,
    private val onAddClick: (Culture) -> Unit,
    private val onRemoveClick: (Culture) -> Unit
) : RecyclerView.Adapter<ManageCulturesAdapter.ViewHolder>() {

    // --- ViewHolder aninhado (geralmente vai aqui dentro) ---
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.iv_culture_manage_image)
        val name: TextView = view.findViewById(R.id.tv_culture_manage_name)
        val btnAdd: ImageButton = view.findViewById(R.id.btn_add_culture)
        val btnRemove: ImageButton = view.findViewById(R.id.btn_remove_culture)
    }

    // ==========================================================
    // ▼▼▼ ADICIONE OS 3 MÉTODOS OBRIGATÓRIOS ABAIXO ▼▼▼
    // ==========================================================

    // 1. Chamado para CRIAR a aparência de cada item da lista (infla o XML)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_culture_manage, parent, false)
        return ViewHolder(view)
    }

    // 2. Chamado para CONECTAR os dados de um item à sua aparência visual
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val culture = cultures[position]
        holder.name.text = culture.name
        holder.image.load(culture.imageUrl) {
            placeholder(R.drawable.ic_leaf)
            error(R.drawable.ic_leaf)
        }

        // Lógica para mostrar/esconder botões
        if (culture.isSelected) {
            holder.btnAdd.visibility = View.GONE
            holder.btnRemove.visibility = View.VISIBLE
        } else {
            holder.btnAdd.visibility = View.VISIBLE
            holder.btnRemove.visibility = View.GONE
        }

        holder.btnAdd.setOnClickListener { onAddClick(culture) }
        holder.btnRemove.setOnClickListener { onRemoveClick(culture) }
    }

    // 3. Chamado para informar o NÚMERO TOTAL de itens na lista
    override fun getItemCount(): Int {
        return cultures.size
    }

    // --- Outras funções do adapter, como updateCultures ---
    fun updateCultures(newCultures: List<Culture>) {
        this.cultures = newCultures
        notifyDataSetChanged()
    }
}