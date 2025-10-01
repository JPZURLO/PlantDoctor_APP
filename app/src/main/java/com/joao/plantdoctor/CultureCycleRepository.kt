package com.joao.plantdoctor

// Um objeto singleton para guardar a lógica de negócios dos ciclos das culturas
object CultureCycleRepository {

    // Esta função retorna o número de dias do ciclo para uma cultura específica
    fun getCycleInDays(cultureName: String): Int? {
        return when (cultureName.lowercase()) {
            "milho" -> 120
            "café" -> 1095 // 3 anos
            "soja" -> 110
            "cana de açúcar" -> 365
            "trigo" -> 150
            "algodão" -> 180
            "arroz" -> 130
            "feijão" -> 90
            "mandioca" -> 270
            "cacau" -> 1825 // 5 anos
            "banana" -> 365
            "laranja" -> 1095 // 3 anos
            else -> null // Retorna nulo se a cultura não for encontrada
        }
    }
}