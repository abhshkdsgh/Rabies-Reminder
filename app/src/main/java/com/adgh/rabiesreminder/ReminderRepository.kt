package com.adgh.rabiesreminder

import kotlinx.coroutines.flow.Flow

class ReminderRepository(private val databaseHandler: DatabaseHandler) {

    val allReminders: Flow<List<MgRecord>> = databaseHandler.allEntries

    suspend fun insert(record: MgRecord): Long {
        return databaseHandler.insert(record)
    }

    suspend fun delete(id: Int) {
        databaseHandler.delete(id)
    }

    suspend fun deleteAll() {
        databaseHandler.deleteAll()
    }

    suspend fun toggleActive(id: Int, isActive: Boolean) {
        databaseHandler.toggleActive(id, isActive)
    }
}
