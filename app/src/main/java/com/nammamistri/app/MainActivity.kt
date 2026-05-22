package com.nammamistri.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.nammamistri.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.pager.adapter = Pages(this)
        TabLayoutMediator(b.tabs, b.pager) { tab, pos ->
            tab.text = when (pos) {
                0 -> getString(R.string.tab_calc)
                1 -> getString(R.string.tab_team)
                2 -> getString(R.string.tab_photos)
                else -> getString(R.string.tab_rates)
            }
        }.attach()
    }

    class Pages(a: FragmentActivity) : FragmentStateAdapter(a) {
        override fun getItemCount() = 4
        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> CalculatorFragment()
            1 -> LaborFragment()
            2 -> PhotosFragment()
            else -> RatesFragment()
        }
    }
}
