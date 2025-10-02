package com.joao.plantdoctor.activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
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
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.navigation.NavigationView
import com.joao.plantdoctor.CultureCycleRepository
import com.joao.plantdoctor.R
import com.joao.plantdoctor.models.OldPlantingsAdapter
import com.joao.plantdoctor.models.HistoryAdapter
import com.joao.plantdoctor.viewmodel.CultureDetailsViewModel
import com.joao.plantdoctor.models.PlantedCulture
import com.joao.plantdoctor.PlantingCalendar // ✅ NOVO: Importa a lógica do calendário
import com.joao.plantdoctor.PlantingWindow // ✅ NOVO: Importa a classe de janela
import java.text.SimpleDateFormat
import java.util.*

// ** OBS: VOCÊ DEVE CRIAR ESTA CLASSE E DECLARÁ-LA NO MANIFEST **
class CameraExamineActivity : AppCompatActivity() {
    // Placeholder para a nova Activity
}

class CultureDetailsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val viewModel: CultureDetailsViewModel by viewModels()
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var recyclerViewHistory: RecyclerView

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var cultureDataScrollView: androidx.core.widget.NestedScrollView

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

        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar)
        cultureDataScrollView = findViewById(R.id.culture_data_scroll_view)

        // --- INICIALIZAÇÃO E CONFIGURAÇÃO DO MENU LATERAL ---
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)
        // --- FIM DA CONFIGURAÇÃO DO MENU LATERAL ---


        cultureId = intent.getIntExtra("CULTURE_ID", -1)
        cultureName = intent.getStringExtra("CULTURE_NAME")
        cultureImageUrl = intent.getStringExtra("CULTURE_IMAGE_URL")

        // Inicializa componentes
        ivCultureImage = findViewById(R.id.iv_culture_detail_image)
        tvPlantingDate = findViewById(R.id.tv_planting_date)
        tvHarvestDate = findViewById(R.id.tv_harvest_date)
        fabAddHistory = findViewById(R.id.fab_add_history)
        btnNewPlantingCycle = findViewById(R.id.btn_new_planting_cycle)
        tvOldPlantingsHeader = findViewById(R.id.tv_old_plantings_header)
        recyclerViewOldPlantings = findViewById(R.id.recycler_view_old_plantings)


        renderCultureDetails(cultureImageUrl)
        setupDrawerHeader()

        setupRecyclerView()
        setupOldPlantingsRecyclerView()
        setupObservers()

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

        drawerLayout.openDrawer(GravityCompat.START)
    }

    // NOVO MÉTODO: Configura o cabeçalho do menu lateral com a imagem da cultura
    private fun setupDrawerHeader() {
        val headerView = navView.getHeaderView(0)

        val headerImageView = headerView.findViewById<ImageView>(R.id.iv_nav_header_culture_image)
        val headerTextView = headerView.findViewById<TextView>(R.id.tv_nav_header_culture_name)

        headerTextView.text = cultureName ?: "Detalhes da Cultura"

        cultureImageUrl?.let { url ->
            headerImageView.load(url) {
                crossfade(true)
                placeholder(R.drawable.ic_leaf)
                error(R.drawable.ic_leaf)
            }
        }
    }


    // MÉTODO OBRIGATÓRIO PARA O MENU LATERAL
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_examine_camera -> {
                // ... (Lógica da Câmera/IA) ...
                val intent = Intent(this, CameraExamineActivity::class.java).apply {
                    putExtra("CULTURE_ID", cultureId)
                }
                startActivity(intent)
            }

            R.id.nav_culture_history -> { // Este item exibe o conteúdo na tela atual
                getToken()?.let { token ->
                    viewModel.fetchAllCulturePlantings(token, cultureId)
                }
                cultureDataScrollView.visibility = View.VISIBLE
                cultureDataScrollView.post { cultureDataScrollView.scrollTo(0, 0) }
                Toast.makeText(this, "Visualizando Histórico de Ciclos.", Toast.LENGTH_SHORT).show()
            }

            R.id.nav_culture_dates -> { // ✅ CORREÇÃO: Este item deve abrir a nova Activity
                val intent = Intent(this, PlantingCalendarActivity::class.java).apply {
                    // Passa o nome para destaque na tela de calendário
                    putExtra("CULTURE_NAME_TO_SCROLL", cultureName)
                }
                startActivity(intent)
                Toast.makeText(this, "Abrindo Calendário de Plantio.", Toast.LENGTH_SHORT).show()
            }

            R.id.nav_delete_planting -> {
                Toast.makeText(this, "Funcionalidade de Exclusão.", Toast.LENGTH_SHORT).show()
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setupOldPlantingsRecyclerView() {
        oldPlantingsAdapter = OldPlantingsAdapter { oldPlantedCulture ->
            showHistoricalData(oldPlantedCulture)
        }

        recyclerViewOldPlantings.adapter = oldPlantingsAdapter
        recyclerViewOldPlantings.layoutManager = LinearLayoutManager(this)
        recyclerViewOldPlantings.isNestedScrollingEnabled = false
        tvOldPlantingsHeader.visibility = View.GONE
        recyclerViewOldPlantings.visibility = View.GONE
    }

    private fun showHistoricalData(oldPlanting: PlantedCulture) {
        if (oldPlanting.id == plantedCultureId) {
            Toast.makeText(this, "Visualizando o ciclo ATIVO.", Toast.LENGTH_SHORT).show()
            fabAddHistory.visibility = View.VISIBLE
            return
        }

        Toast.makeText(this, "Visualizando histórico do plantio em ${oldPlanting.planting_date}.", Toast.LENGTH_LONG).show()

        historyAdapter.submitList(oldPlanting.history_events)

        fabAddHistory.visibility = View.GONE

        cultureDataScrollView.post { cultureDataScrollView.scrollTo(0, 500) }
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

        viewModel.allPlantings.observe(this) { allPlantings ->
            if (allPlantings.size > 1) {
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
                fabAddHistory.visibility = View.VISIBLE
            } else {
                Log.d("CultureDetailsActivity", "Nenhum plantio ativo encontrado.")
                btnNewPlantingCycle.visibility = View.GONE
                fabAddHistory.visibility = View.GONE
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
                fabAddHistory.visibility = View.VISIBLE
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

    // ✅ NOVO: Função para salvar o plantio após a validação
    private fun salvarPlantioConfirmado(dataSelecionada: Calendar, notes: String) {
        plantingDate = dataSelecionada
        updatePlantingDateText()
        calculateAndDisplayHarvestDate()

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dataPlantioFormatada = sdf.format(dataSelecionada.time)
        val token = getToken()

        if (token != null && cultureId != -1) {
            viewModel.saveNewPlanting(token, cultureId, dataPlantioFormatada, notes)
        }
    }

    // ✅ NOVO: Função para verificar se a data está na janela ideal
    private fun isDateIdeal(cultureName: String?, date: Calendar): Boolean {
        if (cultureName == null) return true

        // ✅ Uso de PlantingCalendar.IDEAL_PLANTING_DATES
        val windows = PlantingCalendar.IDEAL_PLANTING_DATES[cultureName] ?: return true
        val selectedMonth = date.get(Calendar.MONTH)

        return windows.any { window ->
            val start = window.startMonth
            val end = window.endMonth

            // Trata janelas que cruzam o final do ano (ex: Outubro-Março)
            if (start > end) {
                selectedMonth >= start || selectedMonth <= end
            } else {
                selectedMonth >= start && selectedMonth <= end
            }
        }
    }

    // ✅ NOVO: Diálogo de Opção de Consulta de Data
    private fun showConsultDateOptionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Consultar Datas")
            .setMessage("Deseja consultar o calendário de plantio ideal para ${cultureName}?")
            .setPositiveButton("Sim, Consultar") { dialog, _ ->
                // ✅ CORREÇÃO: Abre a Activity do Calendário
                val intent = Intent(this, PlantingCalendarActivity::class.java).apply {
                    // Opcional: Passa o nome da cultura para que o calendário role até ela
                    putExtra("CULTURE_NAME_TO_SCROLL", cultureName)
                }
                startActivity(intent)
            }
            .setNegativeButton("Não") { dialog, _ ->
                // Reabre o DatePickerDialog para nova tentativa
                showDatePickerDialog()
            }
            .show()
    }

    // ✅ NOVO: Diálogo de Aviso de Data Fora do Período
    private fun showPlantingWarningDialog(dataSelecionada: Calendar) {
        AlertDialog.Builder(this)
            .setTitle("Aviso: Janela de Plantio")
            .setMessage("A data selecionada está fora do período ideal de plantio para ${cultureName}. Deseja continuar com esta data?")
            .setPositiveButton("Sim, Continuar") { _, _ ->
                salvarPlantioConfirmado(dataSelecionada, "Plantio fora da janela ideal.")
            }
            .setNegativeButton("Não, Mudar Data") { _, _ ->
                showConsultDateOptionDialog()
            }
            .show()
    }

    // ✅ MODIFICADO: Função que agora chama a validação antes de salvar
    private fun processarNovaDataDePlantio(dataSelecionada: Calendar) {
        if (cultureName == null) return

        val isIdeal = isDateIdeal(cultureName, dataSelecionada)

        if (!isIdeal) {
            // DATA FORA DA JANELA: Mostra o diálogo de confirmação
            showPlantingWarningDialog(dataSelecionada)
            return
        }

        // DATA IDEAL: Procede com o salvamento padrão
        salvarPlantioConfirmado(dataSelecionada, "Plantio inicial.")
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedCalendar = Calendar.getInstance().apply { set(selectedYear, selectedMonth, selectedDay) }
            processarNovaDataDePlantio(selectedCalendar) // Chama a validação
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