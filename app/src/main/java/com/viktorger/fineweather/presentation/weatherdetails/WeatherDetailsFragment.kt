package com.viktorger.fineweather.presentation.weatherdetails

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.viktorger.fineweather.app.MyApplication
import com.viktorger.fineweather.databinding.FragmentWeatherDetailsBinding
import com.viktorger.fineweather.domain.model.ResultModel
import com.viktorger.fineweather.presentation.LocationViewModel
import com.viktorger.fineweather.presentation.model.DayEnum
import javax.inject.Inject

class WeatherDetailsFragment : Fragment() {

    private val args: WeatherDetailsFragmentArgs by navArgs()

    private var _binding: FragmentWeatherDetailsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: WeatherDetailsViewModelFactory
    private val vm: WeatherDetailsViewModel by viewModels { viewModelFactory }

    private val vmLocation: LocationViewModel by activityViewModels()

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
        initListeners()
    }

    private fun initListeners() {
        binding.etDetailsChangeicon.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                vm.setImageUrl(textView.text.toString())
                true
            } else {
                false
            }
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

                    binding.tvDetailsMinmax.text = "min ${forecastDay.minTempC}°C • max ${forecastDay.maxTempC}°C"

                    binding.tvDetailsDate.text = forecastDay.date

                    if (forecastDay.hour.isNotEmpty()) {
                        binding.linechartDetails.setTempTimeSource(
                            forecastDay.hour.map { hourModel -> hourModel.tempC },
                            forecastDay.hour.map { hourModel -> hourModel.time },
                            forecastDay.hour.map { hourModel -> hourModel.condition.icon }
                        )
                    }

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

        vmLocation.locationLiveData.observe(viewLifecycleOwner) {

            if (args.day != DayEnum.Nothing) {
                vm.loadForecast(it, args.day)
            }

            binding.srlDetails.setOnRefreshListener {
                vm.updateForecast(it, args.day)
            }
        }

        vm.imageLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResultModel.Success -> Glide.with(this)
                    .load(it.data)
                    .into(binding.imageView)
                is ResultModel.Loading -> { }
                is ResultModel.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
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