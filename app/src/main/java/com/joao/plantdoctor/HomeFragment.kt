package com.joao.plantdoctor.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.joao.plantdoctor.R
import com.joao.plantdoctor.activities.HomeActivity // Lembre-se de usar o nome correto da sua Activity!

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
        // O card_weather também está no seu XML, vamos adicionar o clique para ele também
        val cardWeather = view.findViewById<View>(R.id.card_weather)
        val cardManageUsers = view.findViewById<View>(R.id.card_manage_users)

        // --- NOVO: Lógica para exibir o card de admin ---
        val sharedPrefs = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val userRole = sharedPrefs.getString("USER_ROLE", "COMMON")

        if (userRole == "ADMIN") {
            cardManageUsers.visibility = View.VISIBLE
        } else {
            cardManageUsers.visibility = View.GONE
        }


        // Configura os cliques para cada card
        cardDoubts.setOnClickListener {
            (activity as? HomeActivity)?.navigateTo(DoubtsFragment())
        }

        cardSuggestions.setOnClickListener {
            (activity as? HomeActivity)?.navigateTo(SuggestionsFragment())
        }

        cardRanking.setOnClickListener {
            (activity as? HomeActivity)?.navigateTo(RankingFragment())
        }

        cardMyCultures.setOnClickListener {
            (activity as? HomeActivity)?.navigateTo(MyCulturesFragment())
        }

        cardWeather.setOnClickListener {
            // Supondo que você tenha um WeatherFragment
            (activity as? HomeActivity)?.navigateTo(WeatherFragment())
        }

        // NOVO: Clique do card de admin
        cardManageUsers.setOnClickListener {
            // TODO: Navegar para a tela ManageUsersFragment (que vamos criar)
            Toast.makeText(context, "Abrindo tela de gerenciamento de usuários...", Toast.LENGTH_SHORT).show()
        }

        cardManageUsers.setOnClickListener {
            (activity as? HomeActivity)?.navigateTo(ManageUsersFragment())
        }
    }
}