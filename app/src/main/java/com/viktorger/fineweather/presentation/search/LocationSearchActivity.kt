package com.viktorger.fineweather.presentation.search

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.viktorger.fineweather.app.MyApplication
import com.viktorger.fineweather.data.storage.retrofit.SearchedLocation
import com.viktorger.fineweather.databinding.ActivityLocationSearchBinding
import com.viktorger.fineweather.di.SearchComponent
import com.viktorger.fineweather.domain.model.ResultModel
import com.viktorger.fineweather.presentation.model.LOCATION_SEARCH_KEY
import com.viktorger.fineweather.presentation.model.LocationSearchContract
import com.viktorger.fineweather.presentation.weatherdetails.WeatherDetailsViewModel
import com.viktorger.fineweather.presentation.weatherdetails.WeatherDetailsViewModelFactory
import javax.inject.Inject

class LocationSearchActivity : AppCompatActivity(), SearchAdapter.OnItemClickListener {

    private var _binding: ActivityLocationSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SearchAdapter

    @Inject
    lateinit var viewModelFactory: LocationSearchViewModelFactory
    private val vm: LocationSearchViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[LocationSearchViewModel::class.java]
    }

    lateinit var searchComponent: SearchComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLocationSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchComponent = (application as MyApplication).appComponent.searchComponent().create()
        searchComponent.inject(this)

        // Order makes sense...
        initRv()
        initListeners()
        initEditText()

        onBackPressedDispatcher.addCallback(this) {
            finish()
        }
    }

    private fun initListeners() {
        vm.locationListLiveData.observe(this) {
            when (it) {
                is ResultModel.Success -> {
                    binding.pbSearch.visibility = View.GONE
                    adapter.updateList(it.data)
                }
                is ResultModel.Error -> {
                    binding.pbSearch.visibility = View.GONE
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
                is ResultModel.Loading -> {
                    binding.pbSearch.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun initRv() {
        adapter = SearchAdapter(this)
        binding.rvSearch.adapter = adapter
    }

    private fun initEditText() {
        binding.etSearch.requestFocus()
        binding.tilSearch.setStartIconOnClickListener {
            onBackPressed()
        }

        binding.etSearch.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                vm.updateLocationList(textView.text.toString())
                true
            } else {
                false
            }
        }
    }

    override fun onClick(coordinates: String) {
        val intent = Intent()
        intent.putExtra(LOCATION_SEARCH_KEY, coordinates)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }


}