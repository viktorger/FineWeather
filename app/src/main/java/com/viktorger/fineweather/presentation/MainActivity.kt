package com.viktorger.fineweather.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.viktorger.fineweather.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.viktorger.fineweather.MainGraphDirections
import com.viktorger.fineweather.R
import com.viktorger.fineweather.presentation.model.DayEnum

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.tbMain)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        binding.tlMain.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    when (it.position) {
                        0 -> {
                            val action = MainGraphDirections
                                .actionGlobalWeatherDetailsFragment(DayEnum.Today)
                            navController.navigate(
                                action,
                                navOptions = NavOptions.Builder().setLaunchSingleTop(true).build()
                            )
                        }
                        1 -> {
                            val action = MainGraphDirections
                                .actionGlobalWeatherDetailsFragment(DayEnum.Tomorrow)
                            navController.navigate(
                                action,
                                navOptions = NavOptions.Builder().setLaunchSingleTop(true).build()
                            )
                        }
                        2 -> {
                            navController.navigate(
                                R.id.dailyWeatherFragment,
                                null,
                                navOptions = NavOptions.Builder().setLaunchSingleTop(true).build()
                            )
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.tlMain.getTabAt(0)?.select()

        val action = MainGraphDirections
            .actionGlobalWeatherDetailsFragment(DayEnum.Today)
        navController.navigate(action)
    }
}

