package com.joao.plantdoctor

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater // NOVO: Import necessário
import android.widget.ArrayAdapter // NOVO: Import necessário
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner // NOVO: Import necessário
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import coil.load
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.joao.plantdoctor.models.AtividadeHistorico
import java.text.SimpleDateFormat
import java.util.*

class CultureDetailsActivity : AppCompatActivity() {

    private lateinit var tvPlantingDate: TextView
    private lateinit var tvHarvestDate: TextView
    private lateinit var fabAddHistory: FloatingActionButton
    private var cultureName: String? = null
    private var plantingDate: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_culture_details)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        cultureName = intent.getStringExtra("CULTURE_NAME")
        val cultureImageUrl = intent.getStringExtra("CULTURE_IMAGE_URL")

        val collapsingToolbar = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        collapsingToolbar.title = cultureName

        val ivCultureImage = findViewById<ImageView>(R.id.iv_culture_detail_image)
        ivCultureImage.load(cultureImageUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_leaf)
            error(R.drawable.ic_leaf)
        }

        tvPlantingDate = findViewById(R.id.tv_planting_date)
        tvHarvestDate = findViewById(R.id.tv_harvest_date)
        fabAddHistory = findViewById(R.id.fab_add_history)

        tvPlantingDate.text = "Data de Plantio: (Toque para definir)"
        tvHarvestDate.text = "Colheita Prevista: (Aguardando data de plantio)"

        tvPlantingDate.setOnClickListener {
            showDatePickerDialog()
        }

        fabAddHistory.setOnClickListener {
            showAddHistoryDialog()
        }
    }

    private fun showAddHistoryDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_history, null)
        val spinnerEventType = dialogView.findViewById<Spinner>(R.id.spinner_event_type)
        val editTextObservation = dialogView.findViewById<EditText>(R.id.et_observation)

        val eventTypes = arrayOf("PLANTIO", "ADUBAGEM", "AGROTOXICO", "VENENO", "COLHEITA", "OUTRO")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, eventTypes)
        spinnerEventType.adapter = adapter

        AlertDialog.Builder(this)
            .setTitle("Adicionar Atividade ao Histórico")
            .setView(dialogView)
            .setPositiveButton("Salvar") { dialog, _ ->
                val eventType = spinnerEventType.selectedItem.toString()
                val observation = editTextObservation.text.toString()

                if (observation.isNotEmpty()) {
                    // TODO: Chamar o ViewModel para salvar o novo evento no backend
                    Log.d("HISTORICO", "Tipo: $eventType, Obs: $observation")
                    Toast.makeText(this, "Atividade pronta para ser salva!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "A observação não pode estar vazia.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedCalendar = Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, selectedDay)
            }
            processarNovaDataDePlantio(selectedCalendar)
        }, year, month, day)

        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun processarNovaDataDePlantio(dataSelecionada: Calendar) {
        plantingDate = dataSelecionada
        updatePlantingDateText()
        val dataColheitaPrevista = calculateAndDisplayHarvestDate()
        if (dataColheitaPrevista != null) {
            gerarHistorico(dataColheitaPrevista)
        }
        Toast.makeText(this, "Plantio registrado com sucesso!", Toast.LENGTH_SHORT).show()
    }

    private fun updatePlantingDateText() {
        plantingDate?.let {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            tvPlantingDate.text = "Data de Plantio: ${sdf.format(it.time)}"
        }
    }

    private fun calculateAndDisplayHarvestDate(): String? {
        if (plantingDate == null || cultureName == null) return null

        // AGORA ESTA LINHA FUNCIONA!
        val cycleDays = CultureCycleRepository.getCycleInDays(cultureName!!)

        return if (cycleDays != null) {
            val harvestCalendar = plantingDate!!.clone() as Calendar
            harvestCalendar.add(Calendar.DAY_OF_YEAR, cycleDays)
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val harvestDateString = sdf.format(harvestCalendar.time)
            tvHarvestDate.text = "Colheita Prevista: $harvestDateString (Após $cycleDays dias)"
            harvestDateString
        } else {
            tvHarvestDate.text = "Colheita Prevista: (Ciclo não definido)"
            Toast.makeText(this, "Não há previsão de ciclo para $cultureName", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun gerarHistorico(dataColheitaFormatada: String) {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dataPlantioFormatada = sdf.format(plantingDate!!.time)
        val descricaoPlantio = "Plantio da cultura '${cultureName}' realizado."
        val descricaoColheita = "Colheita prevista para $dataColheitaFormatada."
        val atividadePlantio = AtividadeHistorico(
            id = System.currentTimeMillis(),
            culturaId = 1, // TODO: Substituir pelo ID real da cultura
            descricao = descricaoPlantio,
            data = dataPlantioFormatada
        )
        val atividadeColheita = AtividadeHistorico(
            id = System.currentTimeMillis() + 1,
            culturaId = 1, // TODO: Substituir pelo ID real da cultura
            descricao = descricaoColheita,
            data = dataPlantioFormatada
        )
        Log.d("HISTORICO", "Gerado: ${atividadePlantio.descricao}")
        Log.d("HISTORICO", "Gerado: ${atividadeColheita.descricao}")
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}