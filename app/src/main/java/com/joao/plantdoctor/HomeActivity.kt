package com.joao.plantdoctor

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.joao.plantdoctor.models.Culture

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val fabDiagnose = findViewById<FloatingActionButton>(R.id.fab_diagnose)
        val recyclerViewCultures = findViewById<RecyclerView>(R.id.recycler_view_my_cultures)

        // Configura o botão flutuante
        fabDiagnose.setOnClickListener {
            // TODO: Abrir a câmara para diagnóstico
            Toast.makeText(this, "Abrir câmara para diagnóstico...", Toast.LENGTH_SHORT).show()
        }

        // Configura a lista de culturas do utilizador
        // TODO: Buscar as culturas reais do utilizador guardadas na API
        val myCultures = listOf(
            Culture("Milho", R.drawable.ic_corn_placeholder),
            Culture("Soja", R.drawable.ic_soy_placeholder),
            Culture("Café", R.drawable.ic_coffee_placeholder)
        )

        recyclerViewCultures.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewCultures.adapter = CultureHomeAdapter(myCultures)
    }
}
