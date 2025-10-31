package com.joao.PlantSoS

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    // Define o número total de páginas no onboarding
    override fun getItemCount(): Int = 3

    // Cria o Fragmento para a posição correspondente
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnboardingPageFragment.newInstance(R.layout.onboarding_page_1)
            1 -> OnboardingPageFragment.newInstance(R.layout.onboarding_page_2)
            2 -> OnboardingPageFragment.newInstance(R.layout.onboarding_page_3)
            else -> throw IllegalStateException("Posição inválida no ViewPager")
        }
    }
}