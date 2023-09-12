package com.viktorger.fineweather

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.viktorger.fineweather.databinding.FragmentWeatherDetailsBinding

class WeatherDetailsFragment : Fragment() {
    private var day: Int = 0

    private var _binding: FragmentWeatherDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            day = it.getInt("day")
        }
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

        binding.tvWeatherdetails.text = day.toString()


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

        fun newInstance(day: Day): WeatherDetailsFragment{
            val args = Bundle()
            args.putInt("day", day.dayPosition)
            val fragment = WeatherDetailsFragment()
            fragment.arguments = args
            return fragment
        }
    }
}