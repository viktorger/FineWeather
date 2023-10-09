package com.viktorger.fineweather.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.viktorger.fineweather.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.viktorger.fineweather.MainGraphDirections
import com.viktorger.fineweather.R
import com.viktorger.fineweather.app.MyApplication
import com.viktorger.fineweather.presentation.model.DayEnum
import com.viktorger.fineweather.presentation.model.LocationSearchContract
import com.viktorger.fineweather.presentation.search.LocationSearchActivity
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    @Inject
    lateinit var viewModelFactory: LocationViewModelFactory
    private val vm: LocationViewModel by viewModels { viewModelFactory }

    private val searchActivityLauncher = registerForActivityResult(LocationSearchContract()) {
        it?.let {
            vm.saveResultFromActivity(it)
        }
    }

    val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {

            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as MyApplication).appComponent.inject(this)

        vm.initVm()
        initNavigation()
        initSearchableEditText()
        initObservers()

    }

    private fun initNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

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

    private fun initObservers() {
        vm.locationLiveData.observe(this) {
            binding.etMainSearch.setText(it.locationName)
        }
    }

    private fun initSearchableEditText() {
        binding.etMainSearch.setOnClickListener {
            searchActivityLauncher.launch(null)
        }
    }
}

