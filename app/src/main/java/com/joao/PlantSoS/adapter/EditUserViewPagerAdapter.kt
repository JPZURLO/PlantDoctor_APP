package com.joao.PlantSoS.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.joao.PlantSoS.fragments.EditUserFormFragment
import com.joao.PlantSoS.fragments.UserHistoryFragment
import com.joao.PlantSoS.models.User

class EditUserViewPagerAdapter(fragmentActivity: FragmentActivity, private val user: User) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2 // Duas abas: Informações e Histórico

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> EditUserFormFragment.newInstance(user)
            1 -> UserHistoryFragment.newInstance(user.id)
            else -> throw IllegalStateException("Posição de aba inválida")
        }
    }
}