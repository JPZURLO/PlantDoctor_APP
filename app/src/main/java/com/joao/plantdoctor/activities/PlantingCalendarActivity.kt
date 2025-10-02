// PlantingCalendarActivity.kt

package com.joao.plantdoctor.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joao.plantdoctor.R
import com.joao.plantdoctor.PlantingCalendar // Importe a classe que contém os dados
import com.joao.plantdoctor.models.CulturePlantingAdapter // Crie este adapter!

class PlantingCalendarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planting_calendar) // Crie este layout

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Datas Ideais de Plantio (Brasil)"

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_planting_calendar)

        // Recebe o nome da cultura
        val targetCultureName = intent.getStringExtra("CULTURE_NAME_TO_SCROLL")

        val calendarDataList = PlantingCalendar.IDEAL_PLANTING_DATES.toList()

        val adapter = CulturePlantingAdapter(calendarDataList, targetCultureName) // ✅ Passa o nome para o Adapter

        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        // Lógica para encontrar e rolar
        val targetPosition = calendarDataList.indexOfFirst { it.first == targetCultureName }

        if (targetPosition != -1) {
            // Rola até a posição. Usamos .post para garantir que o layoutManager esteja pronto.
            recyclerView.post {
                // Rola o item para o centro da tela, se possível
                layoutManager.scrollToPositionWithOffset(targetPosition, 0)
            }
        }
    }

    // Garante que o botão de voltar da Toolbar funcione
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}