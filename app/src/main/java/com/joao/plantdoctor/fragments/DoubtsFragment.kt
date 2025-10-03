package com.joao.plantdoctor.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.joao.plantdoctor.R

class DoubtsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Por enquanto, vamos inflar o layout da home, mas idealmente você criará um layout específico
        return inflater.inflate(R.layout.fragment_doubts, container, false)
    }
}