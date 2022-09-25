package com.rodionovmax.materialnasa.ui.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.rodionovmax.materialnasa.R
import com.rodionovmax.materialnasa.databinding.FragmentExploreBinding

class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val exploreViewModel = ViewModelProvider(this)[ExploreViewModel::class.java]

        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = ExplorePagerAdapter(this)
        binding.dotsIndicator.attachTo(binding.viewPager)

        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.setIcon(getTabIcon(position))
            tab.text = getTabTitle(position)
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getTabTitle(position: Int): String {
        return when (position) {
            EARTH_FRAGMENT -> "Earth"
            MARS_FRAGMENT -> "Mars"
            WEATHER_FRAGMENT -> "Weather"
            else -> ""
        }
    }

    private fun getTabIcon(position: Int): Int {
        return when (position) {
            EARTH_FRAGMENT -> R.drawable.ic_earth
            MARS_FRAGMENT -> R.drawable.ic_mars
            WEATHER_FRAGMENT -> R.drawable.ic_system
            else -> throw IndexOutOfBoundsException()
        }
    }
}