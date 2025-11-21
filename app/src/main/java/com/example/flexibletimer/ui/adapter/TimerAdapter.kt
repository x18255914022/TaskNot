package com.example.flexibletimer.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.flexibletimer.data.model.Timer
import com.example.flexibletimer.databinding.ItemTimerBinding
import java.util.concurrent.TimeUnit

class TimerAdapter(
    private val onStartClick: (Timer) -> Unit,
    private val onEditClick: (Timer) -> Unit,
    private val onDeleteClick: (Timer) -> Unit
) : ListAdapter<Timer, TimerAdapter.TimerViewHolder>(TimerDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
        val binding = ItemTimerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        val timer = getItem(position)
        holder.bind(timer, onStartClick, onEditClick, onDeleteClick)
    }

    class TimerViewHolder(private val binding: ItemTimerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(timer: Timer, onStartClick: (Timer) -> Unit, onEditClick: (Timer) -> Unit, onDeleteClick: (Timer) -> Unit) {
            binding.textViewTimerName.text = timer.name
            binding.textViewTimerDuration.text = formatDuration(timer.durationMs)
            binding.buttonStart.setOnClickListener { onStartClick(timer) }
            binding.buttonEdit.setOnClickListener { onEditClick(timer) }
            binding.buttonDelete.setOnClickListener { onDeleteClick(timer) }
        }

        private fun formatDuration(millis: Long): String {
            val hours = TimeUnit.MILLISECONDS.toHours(millis)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
            return String.format("%dh %02dm", hours, minutes)
        }
    }
}

class TimerDiffCallback : DiffUtil.ItemCallback<Timer>() {
    override fun areItemsTheSame(oldItem: Timer, newItem: Timer): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Timer, newItem: Timer): Boolean {
        return oldItem == newItem
    }
}
