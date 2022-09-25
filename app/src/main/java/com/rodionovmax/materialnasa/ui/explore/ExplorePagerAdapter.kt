package com.rodionovmax.materialnasa.ui.explore

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rodionovmax.materialnasa.ui.explore.earth.EarthFragment
import com.rodionovmax.materialnasa.ui.explore.mars.MarsFragment
import com.rodionovmax.materialnasa.ui.explore.weather.WeatherFragment

const val MARS_FRAGMENT = 0
const val EARTH_FRAGMENT = 1
const val WEATHER_FRAGMENT = 2

class ExplorePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragments = arrayListOf(EarthFragment(), MarsFragment(), WeatherFragment())

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> fragments[MARS_FRAGMENT]
            1 -> fragments[EARTH_FRAGMENT]
            2 -> fragments[WEATHER_FRAGMENT]
            else -> throw IndexOutOfBoundsException("Fragment index $position is out of bound")
        }
    }
}