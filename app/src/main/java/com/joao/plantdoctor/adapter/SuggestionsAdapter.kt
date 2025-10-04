package com.joao.plantdoctor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.joao.plantdoctor.R
import com.joao.plantdoctor.models.Suggestion

class SuggestionsAdapter(private var suggestions: List<Suggestion>) : RecyclerView.Adapter<SuggestionsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val author: TextView = view.findViewById(R.id.tv_suggestion_author)
        val suggestion: TextView = view.findViewById(R.id.tv_suggestion_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_suggestion, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val suggestion = suggestions[position]
        holder.author.text = suggestion.author
        holder.suggestion.text = suggestion.suggestion
    }

    override fun getItemCount() = suggestions.size

    fun updateSuggestions(newSuggestions: List<Suggestion>) {
        this.suggestions = newSuggestions
        notifyDataSetChanged()
    }
}