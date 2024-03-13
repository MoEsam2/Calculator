package com.example.calculator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.databinding.ItemResultBinding

class ResultAdapter : RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {
    private val data = mutableListOf<String>()

    class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: String) {
            binding.resultText.text = item
        }

        val binding = ItemResultBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_result, parent, false)
        return ResultViewHolder(itemView)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(data[position])
    }

    fun setData(item: String) {
        data.add(item)
        notifyItemInserted(data.size - 1)
    }

    fun getItems(): List<String> {
        return data.toList()
    }
}