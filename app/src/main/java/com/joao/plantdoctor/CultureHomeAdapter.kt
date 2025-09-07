package com.joao.plantdoctor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.joao.plantdoctor.models.Culture

class CultureHomeAdapter(private val cultures: List<Culture>) :
    RecyclerView.Adapter<CultureHomeAdapter.ViewHolder>() {

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
        holder.image.setImageResource(culture.imageResId)
        holder.name.text = culture.name
    }

    override fun getItemCount() = cultures.size
}
