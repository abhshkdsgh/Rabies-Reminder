package com.adgh.rabiesreminder

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.coroutines.flow.Flow

/**
 * Repository layer wrapping the Room database operations for vaccination records.
 *
 * @property database The underlying [AppDatabase] instance.
 */
class DatabaseHandler(private val database: AppDatabase) {

    /**
     * Cold flow exposing the live list of all [MgRecord] entries sorted by scheduled date ascending.
     */
    val allEntries: Flow<List<MgRecord>> = database.historyDao().getDataFlow()

    /**
     * Fetches a snapshot list of all stored [MgRecord] entries from the database.
     *
     * @return List of all vaccination reminder records.
     */
    suspend fun getAllEntries(): List<MgRecord> {
        return database.historyDao().getData()
    }

    /**
     * Inserts a new vaccination reminder record into the database.
     *
     * @param record The [MgRecord] entry to insert.
     * @return The auto-generated row ID (`id0`) for the inserted record.
     */
    suspend fun insert(record: MgRecord): Long {
        return database.historyDao().insert(record).first()
    }

    /**
     * Deletes a reminder entry matching the given ID.
     *
     * @param id The primary key ID (`id0`) of the record to delete.
     */
    suspend fun delete(id: Int) {
        database.historyDao().delete(id)
    }

    /**
     * Deletes all reminder entries stored in the database.
     */
    suspend fun deleteAll() {
        database.historyDao().deleteAll()
    }

    /**
     * Toggles the active status (`isActive`) of a specific reminder entry.
     *
     * @param id The primary key ID (`id0`) of the target record.
     * @param isActive `true` to enable the reminder alarm, `false` to disable it.
     */
    suspend fun toggleActive(id: Int, isActive: Boolean) {
        database.historyDao().toggleActive(isActive, id)
    }
}

/**
 * Room Entity representing a single anti-rabies vaccination reminder entry in the `History` database table.
 *
 * @property id0 Primary key auto-generated identifier for the record.
 * @property schedule0 The vaccination protocol type ([PEPSchedule.IntraDermal] or [PEPSchedule.IntraMuscular]).
 * @property doseNumber Dose sequence index (e.g. 1, 2, 3, 4, 5).
 * @property long0 Scheduled trigger time in milliseconds since epoch (UTC).
 * @property isPreviousDay `true` if this record is a pre-reminder scheduled 2 days before the actual dose.
 * @property isActive `true` if the alarm trigger is currently enabled.
     */
@Entity(tableName = "History")
data class MgRecord(
    @PrimaryKey(autoGenerate = true) val id0: Int = 0,
    @ColumnInfo(name = "schedule0") val schedule0: PEPSchedule,
    @ColumnInfo(name = "doseNumber") val doseNumber: Int,
    @ColumnInfo(name = "long0") val long0: Long,
    @ColumnInfo(name = "isPreviousDay") val isPreviousDay: Boolean = false,
    @ColumnInfo(name = "isActive") val isActive: Boolean = true
)

/**
 * Room type converters for non-primitive types stored in the database.
 */
class Converters {
    /**
     * Serializes a [PEPSchedule] enum value to a database string.
     *
     * @param value The schedule type enum.
     * @return Corresponding string stored in the database.
     */
    @TypeConverter
    fun fromPEPSchedule(value: PEPSchedule): String {
        return when (value) {
            PEPSchedule.IntraDermal -> "Anti-rabies PEP - Intra-dermal"
            PEPSchedule.IntraMuscular -> "Anti-rabies PEP - Intra-muscular"
        }
    }

    /**
     * Deserializes a database string back to a [PEPSchedule] enum instance.
     *
     * @param value The string stored in the database column.
     * @return Decoded [PEPSchedule] enum instance, defaulting to [PEPSchedule.IntraDermal] for unmapped strings.
     */
    @TypeConverter
    fun toPEPSchedule(value: String): PEPSchedule {
        return when (value) {
            "Anti-rabies PEP - Intra-dermal" -> PEPSchedule.IntraDermal
            "Anti-rabies PEP - Intra-muscular" -> PEPSchedule.IntraMuscular
            else -> PEPSchedule.IntraDermal
        }
    }
}

/**
 * Room Data Access Object (DAO) for operations on the `History` database table.
 */
@Dao
interface HistoryDao {
    /**
     * Returns a cold flow emitting updated lists of [MgRecord] sorted by scheduled timestamp ascending.
     *
     * @return Flow emitting list of records on data change.
     */
    @Query("SELECT * FROM History ORDER BY long0 ASC")
    fun getDataFlow(): Flow<List<MgRecord>>

    /**
     * Retrieves a static snapshot list of all records sorted by scheduled timestamp ascending.
     *
     * @return List of all [MgRecord] entries.
     */
    @Query("SELECT * FROM History ORDER BY long0 ASC")
    suspend fun getData(): List<MgRecord>

    /**
     * Retrieves the primary key ID corresponding to a given trigger timestamp.
     *
     * @param long0 Scheduled timestamp in milliseconds since epoch.
     * @return Matching primary key ID (`id0`).
     */
    @Query("SELECT id0 FROM History WHERE long0= :long0")
    suspend fun getIdFromLong(long0: Long): Int

    /**
     * Deletes a record matching the specified primary key ID.
     *
     * @param id0 Primary key ID to delete.
     */
    @Query("DELETE FROM History WHERE id0 = :id0")
    suspend fun delete(id0: Int)

    /**
     * Deletes all stored records from the database table.
     */
    @Query("DELETE FROM History")
    suspend fun deleteAll()

    /**
     * Inserts one or more records into the `History` table.
     *
     * @param records Variable array of [MgRecord] instances.
     * @return List of generated row primary key IDs.
     */
    @Insert
    suspend fun insert(vararg records: MgRecord): List<Long>

    /**
     * Updates the `isActive` state of a record matching the given ID.
     *
     * @param isActive New active status boolean.
     * @param id Primary key ID (`id0`).
     */
    @Query("UPDATE History SET isActive = :isActive WHERE id0= :id")
    suspend fun toggleActive(isActive: Boolean, id: Int)
}

/**
 * Room Database abstraction providing singleton database instances and access to DAO interfaces.
 */
@Database(
    entities = [MgRecord::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    /** Abstract getter for the [HistoryDao]. */
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Returns the singleton instance of [AppDatabase], creating it if necessary.
         *
         * @param context Context used to build the database builder.
         * @return Thread-safe singleton [AppDatabase] instance.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "XXXXXXXXXXXXXXXX-com.adgh.rabiesreminder"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

