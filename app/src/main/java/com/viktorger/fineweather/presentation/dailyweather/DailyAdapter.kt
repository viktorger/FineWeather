package com.viktorger.fineweather.presentation.dailyweather

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.viktorger.fineweather.databinding.ItemDailyweatherBinding
import com.viktorger.fineweather.domain.model.ForecastDayModel

class DailyAdapter : RecyclerView.Adapter<DailyAdapter.ViewHolder>() {

    private var list = listOf<ForecastDayModel>()

    class ViewHolder(private val binding: ItemDailyweatherBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(forecastDayModel: ForecastDayModel) {
            binding.tvItemdailyDate.text = forecastDayModel.date
            binding.tvItemdailyStatus.text = forecastDayModel.status
            binding.tvItemdailyMaxtemp.text = forecastDayModel.maxTempC.toString()
            binding.tvItemdailyMintemp.text = forecastDayModel.minTempC.toString()

            Glide.with(binding.root)
                .load(forecastDayModel.condition.icon)
                .into(binding.ivItemdailyStatus)
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<ForecastDayModel>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDailyweatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

}