package com.adgh.rabiesreminder

import kotlinx.coroutines.flow.Flow

/**
 * Repository class acting as a single source of truth for vaccination reminder data.
 *
 * Delegates database queries to [DatabaseHandler] and exposes reactive data streams.
 *
 * @property databaseHandler The underlying database handler component.
 */
class ReminderRepository(private val databaseHandler: DatabaseHandler) {

    /** Cold flow exposing live updates of all stored [MgRecord] entries. */
    val allReminders: Flow<List<MgRecord>> = databaseHandler.allEntries

    /**
     * Inserts a new vaccination reminder record into storage.
     *
     * @param record The [MgRecord] entry to persist.
     * @return Auto-generated database primary key ID.
     */
    suspend fun insert(record: MgRecord): Long {
        return databaseHandler.insert(record)
    }

    /**
     * Deletes a reminder entry by its unique ID.
     *
     * @param id Database primary key ID (`id0`).
     */
    suspend fun delete(id: Int) {
        databaseHandler.delete(id)
    }

    /**
     * Deletes all stored reminder entries.
     */
    suspend fun deleteAll() {
        databaseHandler.deleteAll()
    }

    /**
     * Toggles the active state of a specific reminder entry.
     *
     * @param id Primary key ID (`id0`).
     * @param isActive `true` if active, `false` if muted/disabled.
     */
    suspend fun toggleActive(id: Int, isActive: Boolean) {
        databaseHandler.toggleActive(id, isActive)
    }
}

