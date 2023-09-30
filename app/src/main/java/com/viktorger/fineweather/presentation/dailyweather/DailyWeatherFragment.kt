package com.viktorger.fineweather.presentation.dailyweather

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.viktorger.fineweather.databinding.FragmentDailyWeatherBinding
import com.viktorger.fineweather.domain.model.ResultModel

class DailyWeatherFragment : Fragment() {

    val adapter = DailyAdapter()
    private val vm: DailyViewModel by viewModels { DailyViewModelFactory(requireContext()) }

    private var _binding: FragmentDailyWeatherBinding? = null
    private val binding get() = _binding!!

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
        vm.fetchTenDays()

        binding.rvDaily.adapter = adapter
        val currentTimestamp = System.currentTimeMillis()
    }

    private fun initListeners() {
        vm.forecastListLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResultModel.Success -> {
                    adapter.updateList(it.data)
                }
                is ResultModel.Error -> {
                    Toast.makeText(requireContext(),
                        "${it.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}