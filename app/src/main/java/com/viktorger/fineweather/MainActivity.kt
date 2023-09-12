package com.viktorger.fineweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.viktorger.fineweather.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
    private val fragmentsList = listOf(
        WeatherDetailsFragment.newInstance(WeatherDetailsFragment.Companion.Day.Today),
        //WeatherDetailsFragment.newInstance(WeatherDetailsFragment.Companion.Day.Tomorrow),
        LocationSearchFragment(),
        DailyWeatherFragment(),
    )
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.tbMain)

        binding.tlMain.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fl_main, fragmentsList[it.position])
                        .addToBackStack(it.text.toString())
                        .commit()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.tlMain.getTabAt(0)?.select()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_main, fragmentsList[0])
            .commit()

    }
}

