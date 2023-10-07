package com.viktorger.fineweather.presentation.weatherdetails

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.viktorger.fineweather.app.MyApplication
import com.viktorger.fineweather.databinding.FragmentWeatherDetailsBinding
import com.viktorger.fineweather.domain.model.ResultModel
import com.viktorger.fineweather.presentation.model.DayEnum
import javax.inject.Inject

class WeatherDetailsFragment : Fragment() {

    private val args: WeatherDetailsFragmentArgs by navArgs()

    private var _binding: FragmentWeatherDetailsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: WeatherDetailsViewModelFactory
    private val vm: WeatherDetailsViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[WeatherDetailsViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as MyApplication).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherDetailsBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()

        if (args.day != DayEnum.Nothing) {
            vm.loadForecast(args.day)
        }

        binding.srlDetails.setOnRefreshListener {
            vm.updateForecast(args.day)
        }
    }

    private fun initObservers() {
        vm.dayForecastLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResultModel.Success -> {
                    stopLoadingAnims()

                    val forecastDay = it.data

                    if (args.day == DayEnum.Tomorrow) {
                        binding.tvDetailsStatus.textSize = 24f
                        binding.tvDetailsStatus.text = forecastDay.status
                    } else {
                        binding.tvDetailsStatus.textSize = 80f
                        binding.tvDetailsStatus.text = "${forecastDay.status}\u00B0C"
                    }

                    binding.tvDetailsMinmax.text = "min ${forecastDay.minTempC}°C • max ${forecastDay.maxTempC.toInt()}°C "

                    binding.tvDetailsDate.text = forecastDay.date
                    Glide.with(this)
                        .load(forecastDay.condition.icon)
                        .into(binding.imageView)

                    binding.linechartDetails.setTempTimeSource(
                        forecastDay.hour.map { hourModel -> hourModel.tempC },
                        forecastDay.hour.map { hourModel -> hourModel.time },
                        forecastDay.hour.map { hourModel -> hourModel.condition.icon }
                    )

                }
                is ResultModel.Error -> {
                    stopLoadingAnims()

                    Toast.makeText(requireContext(),
                        "${it.message}",
                        Toast.LENGTH_LONG
                    ).show() }
                is ResultModel.Loading -> {
                    binding.pbDetails.visibility = View.VISIBLE
                }
            }

        }
    }

    private fun stopLoadingAnims() {
        binding.srlDetails.isRefreshing = false
        binding.pbDetails.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}