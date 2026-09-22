package com.adgh.rabiesreminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ReminderFilter {
    All,
    New
}

sealed class UiState {
    object Loading : UiState()
    data class Ready(val reminders: List<MgRecord>) : UiState()
    data class Error(val message: String) : UiState()
}

@HiltViewModel
class ReminderUiStateHolder @Inject constructor(private val repository: ReminderRepository) : ViewModel() {

    val uiState: StateFlow<UiState> = repository.allReminders
        .map { UiState.Ready(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )

    fun setActive(id: Int, isActive: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleActive(id, isActive)
        }
    }

    fun delete(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(id)
        }
    }

    suspend fun insert(record: MgRecord): Long {
        return repository.insert(record)
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }
}
