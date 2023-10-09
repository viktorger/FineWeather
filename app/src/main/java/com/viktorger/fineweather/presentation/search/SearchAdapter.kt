package com.viktorger.fineweather.presentation.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.viktorger.fineweather.databinding.ItemSearchedLocationBinding
import com.viktorger.fineweather.domain.model.SearchedLocationModel

class SearchAdapter(private val onItemClickListener: OnItemClickListener)
    : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private var list = listOf<SearchedLocationModel>()

    interface OnItemClickListener {
        fun onClick(searchedLocationModel: SearchedLocationModel)
    }

    inner class ViewHolder(private val binding: ItemSearchedLocationBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SearchedLocationModel) {
            binding.tvItemSearch.text = item.locationName

            binding.root.setOnClickListener{
                onItemClickListener.onClick(item)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchedLocationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<SearchedLocationModel>) {
        this.list = list
        notifyDataSetChanged()
    }
}