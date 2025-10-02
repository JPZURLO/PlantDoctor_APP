package com.joao.plantdoctor

import java.util.Calendar

// ✅ CORRIGIDO: Adiciona a vírgula para separar os argumentos
data class PlantingWindow(
    val startMonth: Int, // Mês inicial (Calendar.JANEIRO = 0, etc.)
    val endMonth: Int // Mês final
)

/**
 * Objeto singleton que armazena as datas ideais de plantio por cultura no Brasil.
 * Os meses são baseados em java.util.Calendar (0=Janeiro, 11=Dezembro).
 */
object PlantingCalendar {

    // 🔴 ESTE É O MAPA DE DADOS QUE O SEU APP VAI CONSULTAR
    val IDEAL_PLANTING_DATES = mapOf(
        "Soja" to listOf(
            // Setembro/Outubro a Dezembro
            PlantingWindow(startMonth = Calendar.SEPTEMBER, endMonth = Calendar.DECEMBER)
        ),
        "Milho" to listOf(
            // 1ª Safra: Setembro/Outubro a Dezembro
            PlantingWindow(startMonth = Calendar.SEPTEMBER, endMonth = Calendar.DECEMBER),
            // 2ª Safra (Safrinha): Janeiro a Março
            PlantingWindow(startMonth = Calendar.JANUARY, endMonth = Calendar.MARCH)
        ),
        "Cana de Açúcar" to listOf(
            // Planta de Ano: Outubro
            PlantingWindow(startMonth = Calendar.OCTOBER, endMonth = Calendar.OCTOBER),
            // Meia Cana: Março/Abril
            PlantingWindow(startMonth = Calendar.MARCH, endMonth = Calendar.APRIL)
        ),
        "Café" to listOf(
            // Setembro a Novembro
            PlantingWindow(startMonth = Calendar.SEPTEMBER, endMonth = Calendar.NOVEMBER)
        ),
        "Algodão" to listOf(
            // Novembro a Fevereiro
            PlantingWindow(startMonth = Calendar.NOVEMBER, endMonth = Calendar.FEBRUARY)
        ),
        "Feijão" to listOf(
            // 1ª Safra (Águas): Setembro a Novembro
            PlantingWindow(startMonth = Calendar.SEPTEMBER, endMonth = Calendar.NOVEMBER),
            // 2ª Safra (Seca): Fevereiro a Março
            PlantingWindow(startMonth = Calendar.FEBRUARY, endMonth = Calendar.MARCH),
            // 3ª Safra (Inverno): Abril a Outubro
            PlantingWindow(startMonth = Calendar.APRIL, endMonth = Calendar.OCTOBER)
        ),
        "Arroz" to listOf(
            // Setembro a Dezembro
            PlantingWindow(startMonth = Calendar.SEPTEMBER, endMonth = Calendar.DECEMBER)
        ),
        "Trigo" to listOf(
            // Maio a Julho
            PlantingWindow(startMonth = Calendar.MAY, endMonth = Calendar.JULY)
        ),
        "Mandioca" to listOf(
            // Setembro a Outubro (Principal)
            PlantingWindow(startMonth = Calendar.SEPTEMBER, endMonth = Calendar.OCTOBER),
            // Março (Nordeste)
            PlantingWindow(startMonth = Calendar.MARCH, endMonth = Calendar.MARCH)
        ),
        "Cacau" to listOf(
            // Início da Estação Chuvosa
            PlantingWindow(startMonth = Calendar.MARCH, endMonth = Calendar.APRIL)
        ),

        "Laranja" to listOf(
            // Outubro a Março
            PlantingWindow(startMonth = Calendar.OCTOBER, endMonth = Calendar.MARCH)
        )
    )
}