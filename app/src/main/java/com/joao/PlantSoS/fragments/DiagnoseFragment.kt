package com.joao.PlantSoS.fragments

import android.Manifest
import android.content.Context // ✅ IMPORT ADICIONADO
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.joao.PlantSoS.R
import com.joao.PlantSoS.activities.CultureDetailsActivity
import com.joao.PlantSoS.models.Culture
// ✅ IMPORT DO VIEWMODEL CORRIGIDO
import com.joao.PlantSoS.viewmodel.CultureDetailsViewModel
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import java.io.File
import java.util.Date

class DiagnoseFragment : Fragment(), CultureSelectListener {

    // ✅ DECLARAÇÃO DO VIEWMODEL CORRIGIDA
    private val viewModel: CultureDetailsViewModel by viewModels()

    // Variáveis para guardar o estado
    private var imageUri: Uri? = null
    private var selectedCulture: Culture? = null

    // --- LAUNCHERS ---

    // 1. Launcher para pedir a PERMISSÃO DE CÂMERA
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permissão dada, mostrar o seletor de culturas
                Log.d("DiagnoseFragment", "Permissão de câmera OK. Mostrando modal.")
                showCultureSelectionModal()
            } else {
                // Permissão negada, avisar o usuário
                Toast.makeText(context, "Permissão de câmera é necessária para o diagnóstico", Toast.LENGTH_LONG).show()
            }
        }

    // 2. Launcher para TIRAR A FOTO
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess: Boolean ->
            if (isSuccess) {
                // Foto tirada com sucesso! 'imageUri' tem o caminho
                imageUri?.let { uri ->
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
                        // Envia o bitmap para a IA
                        runDiagnosis(bitmap)
                    } catch (e: Exception) {
                        Log.e("DiagnoseFragment", "Erro ao carregar bitmap", e)
                        Toast.makeText(context, "Erro ao carregar imagem", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Captura de foto cancelada", Toast.LENGTH_SHORT).show()
            }
        }

    // --- CICLO DE VIDA DO FRAGMENT ---

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_diagnose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Pede a permissão assim que a tela é criada.
        Log.d("DiagnoseFragment", "Pedindo permissão de câmera...")
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    // --- ETAPA 1: SELEÇÃO DE CULTURA ---

    private fun showCultureSelectionModal() {
        // Mostra o modal que criamos (SelectCultureDialogFragment)
        val modal = SelectCultureDialogFragment.newInstance()
        // Define 'this' (o DiagnoseFragment) como o "ouvinte"
        modal.listener = this
        modal.show(childFragmentManager, "SelectCultureModal")
    }

    // Esta função é o callback do modal (da interface CultureSelectListener)
    override fun onCultureSelected(culture: Culture) {
        // ✅ CORRIGIDO: O log de erro indicava 'Unresolved reference: nome'
        // A propriedade correta é 'name' (do seu modelo CultureHomeAdapter)
        Log.d("DiagnoseFragment", "Cultura selecionada: ${culture.name}")
        this.selectedCulture = culture // Salva a cultura

        // Agora que tem a cultura, abre a câmera
        openCamera()
    }

    // --- ETAPA 2: ABRIR A CÂMERA ---

    private fun openCamera() {
        // Cria um arquivo temporário seguro para a foto
        val photoFile: File? = try {
            File.createTempFile("diag_${System.currentTimeMillis()}", ".jpg", requireContext().cacheDir)
        } catch (e: Exception) {
            Log.e("DiagnoseFragment", "Erro ao criar arquivo de foto", e)
            null
        }

        photoFile?.also {
            // Gera uma URI segura para o arquivo (OBRIGATÓRIO para Android 7+)
            imageUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider", // Deve ser o mesmo 'authorities' do Manifest
                it
            )

            Log.d("DiagnoseFragment", "Abrindo câmera. URI da foto: $imageUri")
            // Lança a câmera, passando a URI onde a foto deve ser salva
            takePictureLauncher.launch(imageUri)
        }
    }

    // --- ETAPA 3: RODAR A IA (TFLite) ---

    private fun runDiagnosis(bitmap: Bitmap) {
        try {
            val options = ImageClassifier.ImageClassifierOptions.builder()
                .setMaxResults(1)
                .build()

            // Carrega seu modelo 'model.tflite' da pasta 'assets'
            val classifier = ImageClassifier.createFromFileAndOptions(
                requireContext(),
                "model.tflite",
                options
            )

            val tensorImage = TensorImage.fromBitmap(bitmap)
            val results = classifier.classify(tensorImage)

            if (results.isNotEmpty() && results[0].categories.isNotEmpty()) {
                val bestResult = results[0].categories[0]
                val diagnosisName = bestResult.label
                val confidence = bestResult.score

                val observation = "IA detectou '${diagnosisName}' com ${(confidence * 100).toInt()}% de certeza."

                // Envia para salvar no histórico
                saveAnalysisToHistory(imageUri!!, selectedCulture, diagnosisName, observation)
            } else {
                Toast.makeText(context, "IA não conseguiu identificar a imagem.", Toast.LENGTH_LONG).show()
            }

        } catch (e: Exception) {
            Log.e("DiagnoseFragment", "Erro ao rodar o modelo TFLite", e)
            Toast.makeText(context, "Erro na análise: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // ✅ FUNÇÃO ADICIONADA (necessária para o ViewModel)
    private fun getToken(): String? {
        val sharedPrefs = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        return sharedPrefs.getString("AUTH_TOKEN", null)
    }

    // --- ETAPA 4: SALVAR E NAVEGAR ---

    private fun saveAnalysisToHistory(
        photoUri: Uri,
        culture: Culture?,
        diagnosisName: String,
        observation: String
    ) {
        val cultureId = culture?.id
        val photoPath = photoUri.toString()
        val analysisDate = Date() // Data e hora atual

        Log.d("DiagnoseFragment", "Salvando Análise no ViewModel...")

        // ✅ LÓGICA DE SALVAR CORRIGIDA
        val token = getToken()
        if (token != null && cultureId != null) {
            viewModel.saveNewDiagnosis(
                token = token,
                cultureId = cultureId,
                diagnosisName = diagnosisName,
                observation = observation,
                photoPath = photoPath,
                analysisDate = analysisDate
            )
        } else {
            Toast.makeText(context, "Erro de autenticação ou ID da cultura. Não foi possível salvar.", Toast.LENGTH_LONG).show()
            Log.e("DiagnoseFragment", "Token ou CultureID nulos ao tentar salvar.")
        }
        // --- FIM DA CORREÇÃO ---


        // 1. Mostra o Toast
        Toast.makeText(context, "Análise salva: $diagnosisName", Toast.LENGTH_LONG).show()

        // 2. Verifica se temos uma cultura selecionada para navegar
        if (cultureId != null) {

            // 3. Prepara a navegação
            val intent = Intent(requireContext(), CultureDetailsActivity::class.java)

            // 4. Coloca o ID da cultura no "pacote" (extra) do Intent.
            intent.putExtra("EXTRA_CULTURE_ID", cultureId)

            // 5. Inicia a nova tela
            startActivity(intent)

        } else {
            // Caso algo dê errado e a cultura seja nula
            Log.e("DiagnoseFragment", "Não foi possível navegar, ID da cultura é nulo.")
        }
    }
}


