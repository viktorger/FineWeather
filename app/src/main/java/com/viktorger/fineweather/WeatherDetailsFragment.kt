package com.viktorger.fineweather

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.viktorger.fineweather.databinding.FragmentWeatherDetailsBinding

class WeatherDetailsFragment : Fragment() {

    val args: WeatherDetailsFragmentArgs by navArgs()

    private var _binding: FragmentWeatherDetailsBinding? = null
    private val binding get() = _binding!!

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

        val day = args.day
        binding.tvWeatherdetails.text = day.dayPosition.toString()


    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    companion object {
        enum class Day(val dayPosition: Int) {
            Today(0),
            Tomorrow(1)
        }
    }
}