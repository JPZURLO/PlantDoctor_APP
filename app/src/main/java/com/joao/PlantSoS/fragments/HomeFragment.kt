package com.joao.PlantSoS.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.joao.PlantSoS.R
import com.joao.PlantSoS.activities.HomeActivity

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Apenas infla o layout do menu
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Encontra os cards pelo ID
        val cardDoubts = view.findViewById<View>(R.id.card_doubts)
        val cardSuggestions = view.findViewById<View>(R.id.card_suggestions)
        val cardRanking = view.findViewById<View>(R.id.card_ranking)
        val cardMyCultures = view.findViewById<View>(R.id.card_my_cultures)
        val cardWeather = view.findViewById<View>(R.id.card_weather)
        val cardManageUsers = view.findViewById<View>(R.id.card_manage_users)

        // --- Lógica para exibir o card de admin ---
        val sharedPrefs = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val userRole = sharedPrefs.getString("USER_ROLE", "COMMON")

        if (userRole == "ADMIN") {
            cardManageUsers.visibility = View.VISIBLE
        } else {
            cardManageUsers.visibility = View.GONE
        }

        // --- Configura os cliques para cada card ---

        // Estes abrem SUB-ECRÃS (usando 'navigateTo')
        cardDoubts.setOnClickListener {
            (activity as? HomeActivity)?.navigateTo(DoubtsFragment())
        }

        cardSuggestions.setOnClickListener {
            (activity as? HomeActivity)?.navigateTo(SuggestionsFragment())
        }

        cardRanking.setOnClickListener {
            (activity as? HomeActivity)?.navigateTo(RankingFragment())
        }

        cardManageUsers.setOnClickListener {
            (activity as? HomeActivity)?.navigateTo(ManageUsersFragment())
        }

        // Estes mudam de ABA (usando 'navigateToTab')
        cardMyCultures.setOnClickListener {
            (activity as? HomeActivity)?.navigateToTab(R.id.nav_cultures)
        }
    }
}
