package com.viktorger.fineweather.presentation.dailyweather

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.viktorger.fineweather.app.MyApplication
import com.viktorger.fineweather.databinding.FragmentDailyWeatherBinding
import com.viktorger.fineweather.domain.model.ResultModel
import com.viktorger.fineweather.presentation.LocationViewModel
import javax.inject.Inject

class DailyWeatherFragment : Fragment() {

    private val adapter = DailyAdapter()

    @Inject
    lateinit var viewModelFactory: DailyViewModelFactory
    private val vm: DailyViewModel by viewModels { viewModelFactory }

    private val vmLocation: LocationViewModel by activityViewModels()

    private var _binding: FragmentDailyWeatherBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as MyApplication).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDailyWeatherBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()

        binding.rvDaily.adapter = adapter
    }

    private fun initListeners() {
        vm.forecastListLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResultModel.Success -> {
                    stopLoadingAnims()
                    adapter.updateList(it.data)
                }
                is ResultModel.Error -> {
                    stopLoadingAnims()
                    Toast.makeText(requireContext(),
                        "${it.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                is ResultModel.Loading -> {
                    binding.pbDaily.visibility = View.VISIBLE
                }
            }
        }

        vmLocation.locationLiveData.observe(viewLifecycleOwner) {
            vm.loadTenDaysForecast(it)

            binding.srlDaily.setOnRefreshListener {
                vm.updateTenDaysForecast(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun stopLoadingAnims() {
        binding.srlDaily.isRefreshing = false
        binding.pbDaily.visibility = View.GONE
    }

}