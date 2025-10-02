package com.joao.plantdoctor.models

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.joao.plantdoctor.PlantingWindow
import com.joao.plantdoctor.R
import java.util.Calendar
import java.util.Locale
import java.util.Map
import java.text.SimpleDateFormat
import android.graphics.Color
import androidx.core.content.ContextCompat // ✅ Importação Correta

typealias CalendarEntry = Pair<String, List<PlantingWindow>>

class CulturePlantingAdapter(
    private val calendarData: List<CalendarEntry>,
    private val targetCultureName: String?
) : RecyclerView.Adapter<CulturePlantingAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.tv_calendar_culture_name)
        val windowsTextView: TextView = itemView.findViewById(R.id.tv_calendar_planting_windows)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_planting_calendar, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = calendarData[position]

        // 🔴 CORREÇÃO 1: Acesso correto ao elemento 'key' de um Pair
        val cultureName = entry.first

        holder.nameTextView.text = cultureName

        // Formata a lista de janelas em uma string única
        // 🔴 CORREÇÃO 2: Acesso correto ao elemento 'value' de um Pair (que é o 'second')
        val windowsString = entry.second
            .mapIndexed { index, window -> formatWindow(window, index) }
            .joinToString(" | ")

        holder.windowsTextView.text = windowsString

        // 🟢 CORREÇÃO 3: LÓGICA DE DESTAQUE AGORA DENTRO DA FUNÇÃO E USANDO ContextCompat
        val context = holder.itemView.context

        // Resolve erros de API 23 e Unresolved reference nas cores
        val primaryColor = ContextCompat.getColor(context, R.color.colorPrimary)
        val accentColor = ContextCompat.getColor(context, R.color.colorAccent)
        val accentFaintColor = ContextCompat.getColor(context, R.color.colorAccentFaint)

        if (cultureName == targetCultureName) {
            holder.itemView.setBackgroundColor(accentFaintColor)
            holder.nameTextView.setTextColor(accentColor)
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
            holder.nameTextView.setTextColor(primaryColor)
        }
    }

    override fun getItemCount() = calendarData.size

    // ... (formatMonthName e formatWindow permanecem iguais) ...
    private fun formatMonthName(month: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, month)
        val dateFormat = SimpleDateFormat("MMM", Locale("pt", "BR"))
        return dateFormat.format(calendar.time).replaceFirstChar { it.uppercase(Locale("pt", "BR")) }
    }

    private fun formatWindow(window: PlantingWindow, index: Int): String {
        val startName = formatMonthName(window.startMonth)
        val endName = formatMonthName(window.endMonth)

        val label = if (index == 0) "Principal: " else "${index + 1}ª Safra: "

        return "$label$startName - $endName"
    }
}