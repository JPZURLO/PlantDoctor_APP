package com.joao.PlantSoS.activities

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import com.joao.PlantSoS.R
import com.joao.PlantSoS.models.DiseaseExplanationResponse
import com.joao.PlantSoS.viewmodel.CultureDetailsViewModel // Reutilize o ViewModel

class DiseaseExplanationActivity : AppCompatActivity() {

    // Reutiliza o ViewModel que já tem a lógica de busca
    private val viewModel: CultureDetailsViewModel by viewModels()

    private lateinit var toolbar: Toolbar
    private lateinit var progressBar: ProgressBar
    private lateinit var contentScrollView: NestedScrollView
    private lateinit var tvInvalidMessage: TextView
    private lateinit var tvIdentification: TextView
    private lateinit var tvPrevention: TextView
    private lateinit var tvTreatment: TextView

    private var diseaseName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disease_explanation)

        // 1. Pega o nome da doença vindo da tela anterior (do Adapter)
        diseaseName = intent.getStringExtra("DISEASE_NAME")
        if (diseaseName == null) {
            Toast.makeText(this, "Erro: Nome da doença não fornecido.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // 2. Conecta as Views
        toolbar = findViewById(R.id.toolbar_explanation)
        progressBar = findViewById(R.id.pb_loading)
        contentScrollView = findViewById(R.id.content_scroll_view)
        tvInvalidMessage = findViewById(R.id.tv_invalid_image_message)
        tvIdentification = findViewById(R.id.tv_identification_content)
        tvPrevention = findViewById(R.id.tv_prevention_content)
        tvTreatment = findViewById(R.id.tv_treatment_content)

        // Configura a Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Sobre: ${diseaseName?.replace("_", " ")}"

        // 3. Configura os Observers
        setupObservers()

        // 4. Busca os dados
        viewModel.fetchDiseaseExplanation(diseaseName!!)
        showLoading(true)
    }

    private fun setupObservers() {
        viewModel.diseaseExplanation.observe(this) { result ->
            showLoading(false)
            result.onSuccess { response ->
                // A API retornou a mensagem de "Não é uma planta"
                if (response.mensagem != null) {
                    contentScrollView.visibility = View.GONE
                    tvInvalidMessage.visibility = View.VISIBLE
                    tvInvalidMessage.text = response.mensagem
                } else {
                    // A API retornou os dados da doença
                    contentScrollView.visibility = View.VISIBLE
                    tvInvalidMessage.visibility = View.GONE
                    tvIdentification.text = response.identificacao
                    tvPrevention.text = response.prevencao
                    tvTreatment.text = response.tratamento
                }
            }
            result.onFailure {
                // Falha na API
                contentScrollView.visibility = View.GONE
                tvInvalidMessage.visibility = View.VISIBLE
                tvInvalidMessage.text = "Erro ao carregar dados: ${it.message}"
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        contentScrollView.visibility = if (isLoading) View.VISIBLE else View.GONE

        // Esconde a mensagem de "inválido" enquanto carrega
        if (isLoading) tvInvalidMessage.visibility = View.GONE
    }

    // Para o botão "voltar" da toolbar funcionar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}