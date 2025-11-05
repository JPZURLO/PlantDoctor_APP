package com.joao.PlantSoS.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joao.PlantSoS.models.CultureRankingItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RankingViewModel : ViewModel() {

    // LiveData com resultado de ranking
    private val _rankingData = MutableLiveData<Result<List<CultureRankingItem>>>()
    val rankingData: LiveData<Result<List<CultureRankingItem>>> = _rankingData

    // Função que "busca" os dados (simulação)
    fun fetchRanking() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Simula delay de API
                delay(1000)

                // Simula dados de ranking
                val rankingList = listOf(
                    CultureRankingItem("Milho", 45),
                    CultureRankingItem("Soja", 30),
                    CultureRankingItem("Trigo", 25)
                )

                _rankingData.postValue(Result.success(rankingList))
            } catch (e: Exception) {
                _rankingData.postValue(Result.failure(e))
            }
        }
    }
}
