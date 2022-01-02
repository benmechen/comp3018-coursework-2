package com.psybm7.runningtracker.run

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class RunViewModel(private val repository: RunRepository) : ViewModel() {
    val runs: LiveData<List<Run>> = repository.runs.asLiveData()

    /**
     * Create a new run in a coroutine
     */
    fun insert(run: Run) = viewModelScope.launch {
        repository.insert(run)
    }

    /**
     * Update and existing run in a coroutine
     */
    fun update(run: Run) = viewModelScope.launch {
        repository.update(run)
    }
}

class RunViewModelFactory(private val repository: RunRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RunViewModel::class.java)) {
            return RunViewModel(repository) as T
        }
        throw IllegalArgumentException("Incorrect ViewModel type")
    }
}