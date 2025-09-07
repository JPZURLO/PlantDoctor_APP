package com.joao.plantdoctor

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joao.plantdoctor.models.Culture

class CultureSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_culture_selection)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_cultures)
        val btnConfirm = findViewById<Button>(R.id.btn_confirm_cultures)

        // Lista de culturas de exemplo. Pode vir de uma API no futuro.
        val cultures = listOf(
            Culture("Milho", R.drawable.ic_corn_placeholder),
            Culture("Café", R.drawable.ic_coffee_placeholder),
            Culture("Soja", R.drawable.ic_soy_placeholder),
            Culture("Cana", R.drawable.ic_sugar_cane_placeholder),
            Culture("Trigo", R.drawable.ic_wheat_placeholder),
            Culture("Algodão", R.drawable.ic_cotton_placeholder),
            Culture("Arroz", R.drawable.ic_rice_placeholder),
            Culture("Feijão", R.drawable.ic_bean_placeholder),
            Culture("Mandioca", R.drawable.ic_cassava_placeholder),
            Culture("Cacau", R.drawable.ic_cocoa_placeholder),
            Culture("Banana", R.drawable.ic_banana_placeholder),
            Culture("Laranja", R.drawable.ic_orange_placeholder)
        )

        val adapter = CultureAdapter(cultures)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 2) // Mostra em 2 colunas

        btnConfirm.setOnClickListener {
            val selectedCultures = adapter.getSelectedCultures()
            // TODO: Guardar as culturas selecionadas no perfil do utilizador.
            // Por agora, apenas navegamos para a tela principal.

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}

