package com.example.flexibletimer.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.flexibletimer.data.model.Alert
import com.example.flexibletimer.databinding.ItemAlertBinding
import java.util.concurrent.TimeUnit

class AlertAdapter(
    private val onRemoveClick: (Alert) -> Unit
) : ListAdapter<Alert, AlertAdapter.AlertViewHolder>(AlertDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val binding = ItemAlertBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlertViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val alert = getItem(position)
        holder.bind(alert, onRemoveClick)
    }

    class AlertViewHolder(private val binding: ItemAlertBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(alert: Alert, onRemoveClick: (Alert) -> Unit) {
            binding.textViewAlertTime.text = formatOffset(alert.offsetMs)
            binding.textViewAlertSound.text = alert.soundUri ?: "Default Sound" // Simplified
            binding.buttonRemoveAlert.setOnClickListener { onRemoveClick(alert) }
        }

        private fun formatOffset(millis: Long): String {
            val hours = TimeUnit.MILLISECONDS.toHours(millis)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
            val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
            return String.format("At %02d:%02d:%02d", hours, minutes, seconds)
        }
    }
}

class AlertDiffCallback : DiffUtil.ItemCallback<Alert>() {
    override fun areItemsTheSame(oldItem: Alert, newItem: Alert): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Alert, newItem: Alert): Boolean {
        return oldItem == newItem
    }
}
