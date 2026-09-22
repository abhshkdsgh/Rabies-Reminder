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

/**
 * Filter mode enum used to select which reminder entries are shown on the reminders screen.
 */
enum class ReminderFilter {
    /** Show all entries (past and future). */
    All,

    /** Show only upcoming/future entries. */
    New
}

/**
 * Represents the UI view states for the reminders screen.
 */
sealed class UiState {
    /** Loading state when reminders are being retrieved from the database. */
    object Loading : UiState()

    /**
     * Ready state containing the list of fetched records.
     *
     * @property reminders List of [MgRecord] entries.
     */
    data class Ready(val reminders: List<MgRecord>) : UiState()

    /**
     * Error state indicating a failure to load reminder data.
     *
     * @property message Human-readable error explanation.
     */
    data class Error(val message: String) : UiState()
}

/**
 * ViewModel managing UI state and data operations for anti-rabies vaccination reminders.
 *
 * Injected with [ReminderRepository] using Hilt. Exposes a [StateFlow] emitting UI state changes
 * and handles database updates off the main thread.
 *
 * @property repository Data repository for reminders.
 */
@HiltViewModel
class ReminderUiStateHolder @Inject constructor(private val repository: ReminderRepository) : ViewModel() {

    /**
     * Reactive state stream emitting [UiState] changes to composables.
     * Retains state for 5000ms after subscribers disconnect to handle configuration changes cleanly.
     */
    val uiState: StateFlow<UiState> = repository.allReminders
        .map { UiState.Ready(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )

    /**
     * Updates the active status of a reminder record on an I/O dispatcher.
     *
     * @param id Database primary key ID (`id0`).
     * @param isActive New active status boolean.
     */
    fun setActive(id: Int, isActive: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleActive(id, isActive)
        }
    }

    /**
     * Deletes a reminder entry from the database on an I/O dispatcher.
     *
     * @param id Database primary key ID (`id0`).
     */
    fun delete(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(id)
        }
    }

    /**
     * Inserts a new reminder entry into the database and returns the row ID.
     *
     * @param record The [MgRecord] instance to insert.
     * @return Generated primary key ID.
     */
    suspend fun insert(record: MgRecord): Long {
        return repository.insert(record)
    }

    /**
     * Deletes all reminder records from the database on an I/O dispatcher.
     */
    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }
}

