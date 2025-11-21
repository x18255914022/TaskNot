package com.example.flexibletimer.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.flexibletimer.data.model.Alert
import com.example.flexibletimer.data.model.Timer
import com.example.flexibletimer.data.repository.TimerRepository
import kotlinx.coroutines.launch

class TimerEditViewModel(private val repository: TimerRepository) : ViewModel() {

    private val _timer = MutableLiveData<Timer?>()
    val timer: LiveData<Timer?> = _timer

    private val _alerts = MutableLiveData<List<Alert>>()
    val alerts: LiveData<List<Alert>> = _alerts

    fun loadTimer(timerId: Long) {
        viewModelScope.launch {
            _timer.value = repository.getTimerById(timerId)
            _alerts.value = repository.getAlertsForTimerOnce(timerId)
        }
    }

    fun saveTimer(name: String, durationMs: Long) {
        viewModelScope.launch {
            val timerToSave = Timer(id = _timer.value?.id ?: 0, name = name, durationMs = durationMs)
            val timerId = repository.insertTimer(timerToSave)
            // TODO: Save alerts
            
        }
    }
}

class TimerEditViewModelFactory(private val repository: TimerRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimerEditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TimerEditViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
