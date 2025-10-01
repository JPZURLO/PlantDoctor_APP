package com.joao.plantdoctor.models // Mesmo pacote dos outros modelos

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AtividadeHistorico(
    val id: Long, // Id da atividade
    val culturaId: Int, // Para saber a qual cultura pertence
    val descricao: String,
    val data: String
) : Parcelable