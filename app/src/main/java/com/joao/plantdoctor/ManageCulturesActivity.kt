package com.joao.plantdoctor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ManageCulturesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Esta linha conecta o seu código Kotlin ao layout XML da tela
        setContentView(R.layout.activity_manage_cultures)

        // TODO: Adicione aqui a lógica para buscar as culturas e configurar o RecyclerView
    }
}