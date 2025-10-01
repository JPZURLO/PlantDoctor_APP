package com.joao.plantdoctor.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.joao.plantdoctor.R

class DiagnoseFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // O ID do layout deve ser fragment_diagnose
        return inflater.inflate(R.layout.fragment_diagnose, container, false)
    }
}