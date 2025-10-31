package com.joao.PlantSoS

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

/**
 * Um Fragmento reutilizável para exibir uma única página do onboarding.
 */
class OnboardingPageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla o layout que foi passado como argumento
        val layoutResId = requireArguments().getInt(ARG_LAYOUT_RES_ID)
        return inflater.inflate(layoutResId, container, false)
    }

    companion object {
        private const val ARG_LAYOUT_RES_ID = "layout_res_id"

        /**
         * Cria uma nova instância do fragmento, passando o ID do layout como argumento.
         */
        fun newInstance(@LayoutRes layoutResId: Int): OnboardingPageFragment {
            val fragment = OnboardingPageFragment()
            val args = Bundle()
            args.putInt(ARG_LAYOUT_RES_ID, layoutResId)
            fragment.arguments = args
            return fragment
        }
    }
}
