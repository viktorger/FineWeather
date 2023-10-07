package com.viktorger.fineweather.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.viktorger.fineweather.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.viktorger.fineweather.MainGraphDirections
import com.viktorger.fineweather.R
import com.viktorger.fineweather.presentation.model.DayEnum
import com.viktorger.fineweather.presentation.model.LocationSearchContract
import com.viktorger.fineweather.presentation.search.LocationSearchActivity

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    private val searchActivityLauncher = registerForActivityResult(LocationSearchContract()) {
        it?.let {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        } ?: Toast.makeText(this, "sad", Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        initSearchableEditText()

        binding.tlMain.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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

        onBackPressedDispatcher.addCallback(this) {
            finish()
        }
    }

    private fun initSearchableEditText() {
        binding.etMainSearch.setOnClickListener {
            searchActivityLauncher.launch(null)
        }
    }

}

