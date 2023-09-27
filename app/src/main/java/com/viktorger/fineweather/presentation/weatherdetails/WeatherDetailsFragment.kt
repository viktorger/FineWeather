package com.viktorger.fineweather.presentation.weatherdetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.viktorger.fineweather.databinding.FragmentWeatherDetailsBinding
import com.viktorger.fineweather.domain.model.ResultModel
import com.viktorger.fineweather.presentation.model.DayEnum

class WeatherDetailsFragment : Fragment() {

    val args: WeatherDetailsFragmentArgs by navArgs()

    private var _binding: FragmentWeatherDetailsBinding? = null
    private val binding get() = _binding!!

    private val vm: WeatherDetailsViewModel by viewModels { TodayWeatherDetailsViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()

        if (args.day != DayEnum.Nothing) {
            vm.loadWeather(args.day)
        }


    }

    private fun initObservers() {
        vm.dayForecastLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResultModel.Success -> {
                    binding.pbDetails.visibility = View.GONE

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
                    binding.pbDetails.visibility = View.GONE

                }
                is ResultModel.Loading -> {
                    binding.pbDetails.visibility = View.VISIBLE
                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}