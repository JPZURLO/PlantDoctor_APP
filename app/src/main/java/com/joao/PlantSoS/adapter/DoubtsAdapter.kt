package com.joao.PlantSoS.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.joao.PlantSoS.R
import com.joao.PlantSoS.models.Doubt

class DoubtsAdapter(private var doubts: List<Doubt>) : RecyclerView.Adapter<DoubtsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val author: TextView = view.findViewById(R.id.tv_doubt_author)
        val question: TextView = view.findViewById(R.id.tv_doubt_question)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_doubt, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val doubt = doubts[position]
        holder.author.text = doubt.author
        holder.question.text = doubt.question
    }

    override fun getItemCount() = doubts.size

    fun updateDoubts(newDoubts: List<Doubt>) {
        this.doubts = newDoubts
        notifyDataSetChanged()
    }
}