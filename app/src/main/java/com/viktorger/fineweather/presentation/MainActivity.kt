package com.viktorger.fineweather.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.viktorger.fineweather.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.viktorger.fineweather.MainGraphDirections
import com.viktorger.fineweather.R
import com.viktorger.fineweather.app.MyApplication
import com.viktorger.fineweather.presentation.model.DayEnum
import com.viktorger.fineweather.presentation.model.LocationSearchContract
import com.viktorger.fineweather.presentation.model.NotificationWorker
import java.time.Duration
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

    private val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { _ ->
            vm.initVm()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as MyApplication).appComponent.inject(this)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            vm.initVm()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

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

            Log.d("MainObserver", it.toString())
        }
    }

    private fun initSearchableEditText() {
        binding.etMainSearch.setOnClickListener {
            searchActivityLauncher.launch(null)
        }
    }

    override fun onPause() {
        super.onPause()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(Duration.ofSeconds(5))
                .build()
            WorkManager.getInstance(this).enqueue(workRequest)
        }
    }
}

