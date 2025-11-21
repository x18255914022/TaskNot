package com.example.flexibletimer.ui.activities

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flexibletimer.FlexibleTimerApplication
import com.example.flexibletimer.databinding.ActivityTimerEditBinding
import com.example.flexibletimer.ui.adapter.AlertAdapter
import com.example.flexibletimer.ui.viewmodel.TimerEditViewModel
import com.example.flexibletimer.ui.viewmodel.TimerEditViewModelFactory
import java.util.concurrent.TimeUnit

class TimerEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTimerEditBinding
    private val viewModel: TimerEditViewModel by viewModels {
        TimerEditViewModelFactory((application as FlexibleTimerApplication).repository)
    }
    private lateinit var alertAdapter: AlertAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimerEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        observeViewModel()
        setupClickListeners()

        binding.timePickerDuration.setIs24HourView(true)

        val timerId = intent.getLongExtra(EXTRA_TIMER_ID, -1L)
        if (timerId != -1L) {
            viewModel.loadTimer(timerId)
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        alertAdapter = AlertAdapter { alert ->
            // TODO: Implement alert removal
        }
        binding.recyclerViewAlerts.apply {
            adapter = alertAdapter
            layoutManager = LinearLayoutManager(this@TimerEditActivity)
        }
    }

    private fun observeViewModel() {
        viewModel.timer.observe(this) { timer ->
            timer?.let {
                binding.editTextTimerName.setText(it.name)
                val hours = TimeUnit.MILLISECONDS.toHours(it.durationMs).toInt()
                val minutes = (TimeUnit.MILLISECONDS.toMinutes(it.durationMs) % 60).toInt()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.timePickerDuration.hour = hours
                    binding.timePickerDuration.minute = minutes
                } else {
                    binding.timePickerDuration.currentHour = hours
                    binding.timePickerDuration.currentMinute = minutes
                }
            }
        }
        viewModel.alerts.observe(this) { alerts ->
            alertAdapter.submitList(alerts)
        }
    }

    private fun setupClickListeners() {
        binding.fabSaveTimer.setOnClickListener {
            val name = binding.editTextTimerName.text.toString()
            val durationMs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                TimeUnit.HOURS.toMillis(binding.timePickerDuration.hour.toLong()) +
                        TimeUnit.MINUTES.toMillis(binding.timePickerDuration.minute.toLong())
            } else {
                TimeUnit.HOURS.toMillis(binding.timePickerDuration.currentHour.toLong()) +
                        TimeUnit.MINUTES.toMillis(binding.timePickerDuration.currentMinute.toLong())
            }
            viewModel.saveTimer(name, durationMs)
            finish()
        }
    }

    companion object {
        const val EXTRA_TIMER_ID = "extra_timer_id"
    }
}
