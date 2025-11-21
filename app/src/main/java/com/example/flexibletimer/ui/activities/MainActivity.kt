package com.example.flexibletimer.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flexibletimer.FlexibleTimerApplication
import com.example.flexibletimer.databinding.ActivityMainBinding
import com.example.flexibletimer.ui.adapter.TimerAdapter
import com.example.flexibletimer.ui.viewmodel.MainViewModel
import com.example.flexibletimer.ui.viewmodel.MainViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as FlexibleTimerApplication).repository)
    }
    private lateinit var timerAdapter: TimerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeTimers()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        timerAdapter = TimerAdapter(
            onStartClick = { timer ->
                val intent = Intent(this, TimerService::class.java).apply {
                    action = TimerService.ACTION_START
                    putExtra(TimerService.EXTRA_TIMER_ID, timer.id)
                }
                startService(intent)
            },
            onEditClick = { timer ->
                val intent = Intent(this, TimerEditActivity::class.java).apply {
                    putExtra(TimerEditActivity.EXTRA_TIMER_ID, timer.id)
                }
                startActivity(intent)
            },
            onDeleteClick = { timer ->
                mainViewModel.deleteTimer(timer)
            }
        )
        binding.recyclerViewTimers.apply {
            adapter = timerAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun observeTimers() {
        mainViewModel.allTimers.observe(this) { timers ->
            timers?.let {
                timerAdapter.submitList(it)
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.fabAddTimer.setOnClickListener {
            val intent = Intent(this, TimerEditActivity::class.java)
            startActivity(intent)
        }
    }
}
