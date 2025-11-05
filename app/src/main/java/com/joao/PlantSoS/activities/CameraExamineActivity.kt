package com.joao.PlantSoS.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.joao.PlantSoS.R // <-- Importante
import com.joao.PlantSoS.models.Culture
import com.joao.PlantSoS.fragments.CultureSelectListener
import com.joao.PlantSoS.fragments.SelectCultureDialogFragment
import com.joao.PlantSoS.viewmodel.CultureDetailsViewModel
import java.io.File
import java.util.Date
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.DataType

class CameraExamineActivity : AppCompatActivity(), CultureSelectListener {

    private val viewModel: CultureDetailsViewModel by viewModels()

    // --- Variáveis de Estado ---
    private var imageUri: Uri? = null
    private var selectedCulture: Culture? = null
    private var lastDiagnosisName: String? = null
    private var lastObservation: String? = null

    // --- Variáveis da IA ---
    private lateinit var tflite: Interpreter
    private lateinit var labels: List<String>
    private val inputImageWidth = 224
    private val inputImageHeight = 224

    // --- Views do Layout ---
    private lateinit var ivPhoto: ImageView
    private lateinit var tvDiagnosisName: TextView
    private lateinit var tvObservation: TextView
    private lateinit var btnSave: Button
    private lateinit var pbLoading: ProgressBar

    // --- LAUNCHERS ---

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("CameraExamineActivity", "Permissão OK.")
                handlePermissionGranted()
            } else {
                Toast.makeText(this, "Permissão de câmera é necessária", Toast.LENGTH_LONG).show()
                finish()
            }
        }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess: Boolean ->
            if (isSuccess) {
                imageUri?.let { uri ->
                    try {
                        val bitmap: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            val source = ImageDecoder.createSource(this.contentResolver, uri)
                            ImageDecoder.decodeBitmap(source).copy(Bitmap.Config.ARGB_8888, true)
                        } else {
                            @Suppress("DEPRECATION")
                            MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                        }

                        // 1. Mostra a foto e o loading
                        ivPhoto.setImageBitmap(bitmap)
                        pbLoading.visibility = View.VISIBLE
                        tvDiagnosisName.text = "Analisando..."
                        tvObservation.text = ""

                        // 2. Roda a IA (agora com o bitmap e a uri)
                        runDiagnosis(bitmap, uri)

                    } catch (e: Exception) {
                        Log.e("CameraExamineActivity", "Erro ao carregar bitmap", e)
                        finish()
                    }
                }
            } else {
                Toast.makeText(this, "Captura de foto cancelada", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

    // --- CICLO DE VIDA ---

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. LIGA A TELA AO XML
        setContentView(R.layout.activity_camera_examine)

        // 2. ENCONTRA AS VIEWS
        ivPhoto = findViewById(R.id.iv_diagnosis_photo)
        tvDiagnosisName = findViewById(R.id.tv_diagnosis_result_name)
        tvObservation = findViewById(R.id.tv_diagnosis_observation)
        btnSave = findViewById(R.id.btn_save_diagnosis)
        pbLoading = findViewById(R.id.pb_loading_analysis)
        val toolbar = findViewById<Toolbar>(R.id.toolbar_examine)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // --- Carrega o modelo e as labels ---
        try {
            tflite = Interpreter(loadModelFile())
            labels = FileUtil.loadLabels(this, "labels.txt")
        } catch (e: Exception) {
            Log.e("CameraExamineActivity", "Erro ao inicializar TFLite", e)
            Toast.makeText(this, "Erro ao carregar modelo de IA", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        // --- Fim do carregamento ---

        Log.d("CameraExamineActivity", "Iniciando fluxo de diagnóstico...")

        // Tenta pegar a cultura que a CultureDetailsActivity mandou
        val cultureId = intent.getIntExtra("CULTURE_ID", -1)
        val cultureName = intent.getStringExtra("CULTURE_NAME")
        val cultureImageUrl = intent.getStringExtra("CULTURE_IMAGE_URL")

        if (cultureId != -1 && cultureName != null) {
            Log.d("CameraExamineActivity", "Fluxo 2: Cultura pré-selecionada (ID: $cultureId)")
            try {
                this.selectedCulture = Culture(
                    id = cultureId, name = cultureName, imageUrl = cultureImageUrl ?: "",
                    cicloEmDias = 0, dataColheitaPrevista = "", dataPlantio = ""
                )
            } catch (e: Exception) {
                Log.e("CameraExamine", "Falha ao criar objeto Culture. Verifique os tipos de dados!", e)
                finish()
            }
        } else {
            Log.d("CameraExamineActivity", "Fluxo 1: Nenhuma cultura selecionada.")
            this.selectedCulture = null
        }

        // Configura o clique do botão Salvar
        btnSave.setOnClickListener {
            saveAnalysisToHistory()
        }

        // Configura o observer (para saber quando o salvamento terminou)
        viewModel.diagnosisSaveResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Diagnóstico salvo com sucesso!", Toast.LENGTH_SHORT).show()
                finish() // Fecha a tela e volta para CultureDetails
            }
            result.onFailure {
                Toast.makeText(this, "Falha ao salvar: ${it.message}", Toast.LENGTH_LONG).show()
                btnSave.isEnabled = true // Reabilita o botão se falhar
            }
        }

        startDiagnosisFlow()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::tflite.isInitialized) {
            tflite.close()
        }
    }

    // Para o botão "voltar" da toolbar funcionar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    // --- LÓGICA CENTRAL ---

    private fun startDiagnosisFlow() {
        when (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
            PackageManager.PERMISSION_GRANTED -> {
                Log.d("CameraExamineActivity", "Permissão já garantida.")
                handlePermissionGranted()
            }
            else -> {
                Log.d("CameraExamineActivity", "Pedindo permissão de câmera...")
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun handlePermissionGranted() {
        if (this.selectedCulture != null) {
            Log.d("CameraExamineActivity", "Indo direto para a câmera.")
            openCamera()
        } else {
            Log.d("CameraExamineActivity", "Mostrando modal de seleção.")
            showCultureSelectionModal()
        }
    }

    // --- MÉTODOS ---

    private fun showCultureSelectionModal() {
        val modal = SelectCultureDialogFragment.newInstance()
        modal.listener = this
        modal.show(supportFragmentManager, "SelectCultureModal")
    }

    override fun onCultureSelected(culture: Culture) {
        Log.d("CameraExamineActivity", "Cultura selecionada: ${culture.name}")
        this.selectedCulture = culture
        openCamera()
    }

    private fun openCamera() {
        val photoFile: File? = try {
            File.createTempFile("diag_${System.currentTimeMillis()}", ".jpg", this.cacheDir)
        } catch (e: Exception) {
            Log.e("CameraExamineActivity", "Erro ao criar arquivo de foto", e)
            null
        }

        photoFile?.also {
            val localImageUri = FileProvider.getUriForFile(
                this,
                "${this.packageName}.provider",
                it
            )
            this.imageUri = localImageUri
            Log.d("CameraExamineActivity", "Abrindo câmera...")
            takePictureLauncher.launch(localImageUri)
        }
    }

    /**
     * Função auxiliar para carregar o 'model.tflite' da pasta 'assets'
     */
    private fun loadModelFile(): ByteBuffer {
        val fileDescriptor = assets.openFd("model.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    /**
     * Roda a IA e MOSTRA O RESULTADO NA TELA
     */
    private fun runDiagnosis(bitmap: Bitmap, photoUri: Uri) {
        try {
            val imageProcessor = ImageProcessor.Builder()
                .add(ResizeOp(inputImageHeight, inputImageWidth, ResizeOp.ResizeMethod.BILINEAR))
                .add(NormalizeOp(127.5f, 127.5f))
                .build()

            var tensorImage = TensorImage(DataType.FLOAT32)
            tensorImage.load(bitmap)
            tensorImage = imageProcessor.process(tensorImage)
            val inputBuffer = tensorImage.buffer
            val outputBuffer = Array(1) { FloatArray(labels.size) }
            tflite.run(inputBuffer, outputBuffer)

            val results = outputBuffer[0]
            var maxConfidence = -1.0f
            var maxIndex = -1
            for (i in labels.indices) {
                if (results[i] > maxConfidence) {
                    maxConfidence = results[i]
                    maxIndex = i
                }
            }

            pbLoading.visibility = View.GONE // Esconde o loading

            if (maxIndex != -1) {
                val diagnosisName = labels[maxIndex]
                val confidence = maxConfidence
                val cleanDiagnosisName = diagnosisName.substringAfter(" ")
                val observation = "IA detectou '${cleanDiagnosisName}' com ${(confidence * 100).toInt()}% de certeza."

                // 1. MOSTRA os resultados na tela
                tvDiagnosisName.text = cleanDiagnosisName
                tvObservation.text = observation

                // 2. GUARDA os resultados para salvar
                this.lastDiagnosisName = cleanDiagnosisName
                this.lastObservation = observation
                this.imageUri = photoUri

                // 3. MOSTRA o botão de salvar
                btnSave.visibility = View.VISIBLE

            } else {
                tvDiagnosisName.text = "Falha na Análise"
                tvObservation.text = "IA não conseguiu identificar a imagem."
            }

        } catch (e: Exception) {
            Log.e("CameraExamineActivity", "Erro ao rodar TFLite (Interpreter)", e)
            tvDiagnosisName.text = "Erro"
            tvObservation.text = "Ocorreu um erro na análise: ${e.message}"
            pbLoading.visibility = View.GONE
        }
    }

    private fun getToken(): String? {
        val sharedPrefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        return sharedPrefs.getString("AUTH_TOKEN", null)
    }

    /**
     * Pega os resultados guardados e envia para o ViewModel salvar.
     */
    private fun saveAnalysisToHistory() {
        val cultureId = selectedCulture?.id
        val token = getToken()
        val name = lastDiagnosisName
        val obs = lastObservation
        val uri = imageUri

        if (token != null && cultureId != null && name != null && obs != null && uri != null) {
            btnSave.isEnabled = false // Desabilita o botão para não clicar 2x
            viewModel.saveNewDiagnosis(
                token = token,
                cultureId = cultureId,
                diagnosisName = name,
                observation = obs,
                photoPath = uri.toString(),
                analysisDate = Date()
            )
        } else {
            Toast.makeText(this, "Erro: Dados do diagnóstico estão incompletos.", Toast.LENGTH_LONG).show()
            Log.e("CameraExamineActivity", "Token, CultureID, Nome ou URI nulos na hora de salvar.")
        }
    }
}