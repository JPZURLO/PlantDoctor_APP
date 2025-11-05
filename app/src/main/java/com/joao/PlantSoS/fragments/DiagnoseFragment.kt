package com.joao.PlantSoS.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.joao.PlantSoS.R
import com.joao.PlantSoS.activities.CameraExamineActivity

class DiagnoseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_diagnose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnStart = view.findViewById<Button>(R.id.btn_start_diagnosis)
        btnStart.setOnClickListener {
            Log.d("DiagnoseFragment", "Botão clicado. Lançando CameraExamineActivity...")
            val intent = Intent(requireContext(), CameraExamineActivity::class.java)
            startActivity(intent)
        }
    }
}
