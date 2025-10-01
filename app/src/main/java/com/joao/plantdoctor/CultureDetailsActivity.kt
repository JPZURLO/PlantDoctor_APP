package com.joao.plantdoctor.activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import android.widget.Button
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.joao.plantdoctor.CultureCycleRepository
import com.joao.plantdoctor.R
// Você precisará criar estas classes/arquivos:
import com.joao.plantdoctor.models.OldPlantingsAdapter // NOVO: Adapter para os plantios antigos
import com.joao.plantdoctor.models.HistoryAdapter
import com.joao.plantdoctor.viewmodel.CultureDetailsViewModel
import java.text.SimpleDateFormat
import java.util.*

class CultureDetailsActivity : AppCompatActivity() {

    private val viewModel: CultureDetailsViewModel by viewModels()
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var recyclerViewHistory: RecyclerView

    // NOVO: Componentes para Plantios Antigos
    private lateinit var tvOldPlantingsHeader: TextView
    private lateinit var recyclerViewOldPlantings: RecyclerView
    private lateinit var oldPlantingsAdapter: OldPlantingsAdapter

    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var ivCultureImage: ImageView

    private lateinit var tvPlantingDate: TextView
    private lateinit var tvHarvestDate: TextView
    private lateinit var fabAddHistory: FloatingActionButton
    private lateinit var btnNewPlantingCycle: Button

    private var cultureId: Int = -1
    private var plantedCultureId: Int = -1
    private var cultureName: String? = null
    private var cultureImageUrl: String? = null
    private var plantingDate: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_culture_details)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar)

        cultureId = intent.getIntExtra("CULTURE_ID", -1)
        cultureName = intent.getStringExtra("CULTURE_NAME")
        cultureImageUrl = intent.getStringExtra("CULTURE_IMAGE_URL")

        ivCultureImage = findViewById(R.id.iv_culture_detail_image)
        tvPlantingDate = findViewById(R.id.tv_planting_date)
        tvHarvestDate = findViewById(R.id.tv_harvest_date)
        fabAddHistory = findViewById(R.id.fab_add_history)
        btnNewPlantingCycle = findViewById(R.id.btn_new_planting_cycle)

        // NOVO: Inicializa componentes e RecyclerViews de Plantios Antigos
        tvOldPlantingsHeader = findViewById(R.id.tv_old_plantings_header) // Crie este ID no XML
        recyclerViewOldPlantings = findViewById(R.id.recycler_view_old_plantings) // Crie este ID no XML

        renderCultureDetails(cultureImageUrl)

        setupRecyclerView()
        setupOldPlantingsRecyclerView() // NOVO: Configuração do novo RecyclerView
        setupObservers()

        getToken()?.let {
            if (cultureId != -1) {
                // CHAMA A NOVA FUNÇÃO DE BUSCA COMPLETA
                viewModel.fetchAllCulturePlantings(it, cultureId)
            }
        }

        tvPlantingDate.text = "Data de Plantio: (Toque para definir)"
        tvHarvestDate.text = "Colheita Prevista: (Aguardando data de plantio)"
        tvPlantingDate.setOnClickListener { showDatePickerDialog() }
        fabAddHistory.setOnClickListener { showAddHistoryDialog() }

        btnNewPlantingCycle.setOnClickListener {
            viewModel.clearState()

            this.plantedCultureId = -1
            this.plantingDate = null
            updatePlantingDateText()
            calculateAndDisplayHarvestDate()
            historyAdapter.submitList(emptyList())

            Toast.makeText(this, "Inicie o novo ciclo, selecione a Data de Plantio.", Toast.LENGTH_LONG).show()
            showDatePickerDialog()
        }
    }

    // NOVO: Configura o RecyclerView para plantios antigos
    private fun setupOldPlantingsRecyclerView() {
        oldPlantingsAdapter = OldPlantingsAdapter()
        recyclerViewOldPlantings.adapter = oldPlantingsAdapter
        recyclerViewOldPlantings.layoutManager = LinearLayoutManager(this)
        recyclerViewOldPlantings.isNestedScrollingEnabled = false
        tvOldPlantingsHeader.visibility = View.GONE
        recyclerViewOldPlantings.visibility = View.GONE
    }

    private fun renderCultureDetails(imageUrl: String?) {
        collapsingToolbarLayout.title = cultureName ?: "Detalhes da Cultura"

        imageUrl?.let { url ->
            ivCultureImage.load(url) {
                crossfade(true)
                placeholder(R.drawable.ic_leaf)
                error(R.drawable.ic_leaf)
            }
        }
    }

    private fun getToken(): String? {
        val sharedPrefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        return sharedPrefs.getString("AUTH_TOKEN", null)
    }

    private fun setupRecyclerView() {
        recyclerViewHistory = findViewById(R.id.recycler_view_history)
        historyAdapter = HistoryAdapter()
        recyclerViewHistory.adapter = historyAdapter
        recyclerViewHistory.layoutManager = LinearLayoutManager(this)
    }

    private fun setupObservers() {

        // NOVO OBSERVER: Gerencia a lista completa e a exibe
        viewModel.allPlantings.observe(this) { allPlantings ->
            if (allPlantings.size > 1) {
                // Pega todos, exceto o primeiro (que é o plantio ativo / mais recente)
                val oldPlantings = allPlantings.drop(1)
                oldPlantingsAdapter.submitList(oldPlantings)

                tvOldPlantingsHeader.visibility = View.VISIBLE
                recyclerViewOldPlantings.visibility = View.VISIBLE
                tvOldPlantingsHeader.text = "Plantios Anteriores (${oldPlantings.size})"
            } else {
                tvOldPlantingsHeader.visibility = View.GONE
                recyclerViewOldPlantings.visibility = View.GONE
            }
        }

        viewModel.historyList.observe(this) { history ->
            historyAdapter.submitList(history)
        }

        viewModel.plantedCultureDetail.observe(this) { existingPlanting ->
            // ESTA PARTE SÓ LIDA COM O PLANTIO ATIVO/MAIS RECENTE
            if (existingPlanting != null) {
                Toast.makeText(this, "Carregando dados do plantio mais recente...", Toast.LENGTH_SHORT).show()
                this.plantedCultureId = existingPlanting.id

                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                try {
                    val calendar = Calendar.getInstance()
                    calendar.time = sdf.parse(existingPlanting.planting_date)!!
                    this.plantingDate = calendar
                } catch (e: Exception) {
                    Log.e("CultureDetailsActivity", "Erro ao converter data", e)
                    return@observe
                }
                updatePlantingDateText()
                calculateAndDisplayHarvestDate()
                btnNewPlantingCycle.visibility = View.VISIBLE
            } else {
                Log.d("CultureDetailsActivity", "Nenhum plantio ativo encontrado.")
                btnNewPlantingCycle.visibility = View.GONE
                this.plantedCultureId = -1
                this.plantingDate = null
                updatePlantingDateText()
                calculateAndDisplayHarvestDate()
            }
        }

        viewModel.newlyPlantedCulture.observe(this) { result ->
            result.onSuccess { newPlanting ->
                Toast.makeText(this, "Plantio registrado com ID: ${newPlanting.id}", Toast.LENGTH_SHORT).show()

                this.plantedCultureId = newPlanting.id

                val token = getToken()!!
                val descricaoPlantio = "Plantio da cultura '${cultureName}' realizado."
                viewModel.saveHistoryEvent(token, plantedCultureId, "PLANTIO", descricaoPlantio)

                val dataColheitaPrevista = calculateAndDisplayHarvestDate()
                if (dataColheitaPrevista != null) {
                    val descricaoColheita = "Colheita prevista para $dataColheitaPrevista."
                    viewModel.saveHistoryEvent(token, plantedCultureId, "OUTRO", descricaoColheita)
                }
                btnNewPlantingCycle.visibility = View.VISIBLE
            }
            result.onFailure {
                Toast.makeText(this, "Erro CRÍTICO ao registrar plantio: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.saveResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Atividade salva com sucesso!", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(this, "Erro ao salvar atividade: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showAddHistoryDialog() {
        // ... (Seu código para showAddHistoryDialog) ...
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_history, null)
        val spinnerEventType = dialogView.findViewById<Spinner>(R.id.spinner_event_type)
        val editTextObservation = dialogView.findViewById<EditText>(R.id.et_observation)
        val eventTypes = arrayOf("ADUBAGEM", "AGROTOXICO", "VENENO", "COLHEITA", "OUTRO")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, eventTypes)
        spinnerEventType.adapter = adapter

        AlertDialog.Builder(this)
            .setTitle("Adicionar Atividade ao Histórico")
            .setView(dialogView)
            .setPositiveButton("Salvar") { _, _ ->
                val eventType = spinnerEventType.selectedItem.toString()
                val observation = editTextObservation.text.toString()
                val token = getToken()

                if (token == null) {
                    Toast.makeText(this, "Erro de autenticação", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                if (observation.isEmpty()) {
                    Toast.makeText(this, "A observação não pode estar vazia.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                if (plantedCultureId == -1) {
                    Toast.makeText(this, "Defina uma data de plantio primeiro.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                viewModel.saveHistoryEvent(token, plantedCultureId, eventType, observation)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun processarNovaDataDePlantio(dataSelecionada: Calendar) {
        plantingDate = dataSelecionada
        updatePlantingDateText()
        calculateAndDisplayHarvestDate()

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dataPlantioFormatada = sdf.format(dataSelecionada.time)
        val token = getToken()

        if (token != null && cultureId != -1) {
            viewModel.saveNewPlanting(token, cultureId, dataPlantioFormatada, "Plantio inicial.")
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedCalendar = Calendar.getInstance().apply { set(selectedYear, selectedMonth, selectedDay) }
            processarNovaDataDePlantio(selectedCalendar)
        }, year, month, day)

        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun updatePlantingDateText() {
        plantingDate?.let {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            tvPlantingDate.text = "Data de Plantio: ${sdf.format(it.time)}"
        } ?: run {
            tvPlantingDate.text = "Data de Plantio: (Toque para definir)"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun calculateAndDisplayHarvestDate(): String? {
        if (plantingDate == null || cultureName == null) {
            tvHarvestDate.text = "Colheita Prevista: (Aguardando data de plantio)"
            return null
        }

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
}