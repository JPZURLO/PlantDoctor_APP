package com.joao.PlantSoS.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.joao.PlantSoS.R
import com.joao.PlantSoS.viewmodel.RankingViewModel

class RankingFragment : Fragment() {

    private val viewModel: RankingViewModel by viewModels()
    private lateinit var pieChart: PieChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ranking, container, false)
        pieChart = view.findViewById(R.id.pie_chart)

        setupChart()
        setupObservers()

        return view
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchRanking()
    }

    private fun setupChart() {
        pieChart.isDrawHoleEnabled = true
        pieChart.setUsePercentValues(true)
        pieChart.setEntryLabelTextSize(12f)
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.centerText = "Culturas"
        pieChart.setCenterTextSize(24f)
        pieChart.setCenterTextColor(Color.WHITE)
        pieChart.description.isEnabled = false

        val legend = pieChart.legend
        legend.textColor = Color.WHITE
        legend.textSize = 14f
    }

    private fun setupObservers() {
        viewModel.rankingData.observe(viewLifecycleOwner) { result ->
            result.onSuccess { rankingList ->
                if (rankingList.isEmpty()) {
                    Toast.makeText(context, "Nenhum dado de ranking encontrado.", Toast.LENGTH_SHORT).show()
                    pieChart.clear()
                    return@onSuccess
                }

                val entries = ArrayList<PieEntry>()
                for (item in rankingList) {
                    entries.add(PieEntry(item.count.toFloat(), item.culture))
                }

                val colors = ArrayList<Int>()
                colors.addAll(ColorTemplate.MATERIAL_COLORS.toList())
                colors.addAll(ColorTemplate.VORDIPLOM_COLORS.toList())

                val dataSet = PieDataSet(entries, "")
                dataSet.colors = colors
                dataSet.valueTextColor = Color.BLACK
                dataSet.valueTextSize = 16f
                dataSet.valueFormatter = PercentFormatter(pieChart)

                pieChart.data = PieData(dataSet)
                pieChart.invalidate()
            }
            result.onFailure { error ->
                Toast.makeText(context, "Erro ao carregar ranking: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
