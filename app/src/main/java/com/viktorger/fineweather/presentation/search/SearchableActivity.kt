package com.viktorger.fineweather.presentation.search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.viktorger.fineweather.R
import com.viktorger.fineweather.databinding.ActivitySearchableBinding

class SearchableActivity : AppCompatActivity() {

    private var _binding: ActivitySearchableBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Intent.ACTION_SEARCH == intent.action) {
            
        }
    }
}