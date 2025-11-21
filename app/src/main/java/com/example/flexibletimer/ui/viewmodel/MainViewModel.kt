package com.example.flexibletimer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.flexibletimer.data.model.Timer
import com.example.flexibletimer.data.repository.TimerRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: TimerRepository) : ViewModel() {

    val allTimers = repository.getAllTimers().asLiveData()

    fun deleteTimer(timer: Timer) = viewModelScope.launch {
        repository.deleteTimer(timer)
    }
}

class MainViewModelFactory(private val repository: TimerRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
