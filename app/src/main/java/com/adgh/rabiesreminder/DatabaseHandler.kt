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

class DatabaseHandler(private val database: AppDatabase) {

    val allEntries: Flow<List<MgRecord>> = database.historyDao().getDataFlow()

    suspend fun getAllEntries(): List<MgRecord> {
        return database.historyDao().getData()
    }

    suspend fun insert(record: MgRecord): Long {
        return database.historyDao().insert(record).first()
    }

    suspend fun delete(id: Int) {
        database.historyDao().delete(id)
    }

    suspend fun deleteAll() {
        database.historyDao().deleteAll()
    }

    suspend fun toggleActive(id: Int, isActive: Boolean) {
        database.historyDao().toggleActive(isActive, id)
    }
}

@Entity(tableName = "History")
data class MgRecord(
    @PrimaryKey(autoGenerate = true) val id0: Int = 0,
    @ColumnInfo(name = "schedule0") val schedule0: PEPSchedule,
    @ColumnInfo(name = "doseNumber") val doseNumber: Int,
    @ColumnInfo(name = "long0") val long0: Long,
    @ColumnInfo(name = "isPreviousDay") val isPreviousDay: Boolean = false,
    @ColumnInfo(name = "isActive") val isActive: Boolean = true
)

class Converters {
    @TypeConverter
    fun fromPEPSchedule(value: PEPSchedule): String {
        return when (value) {
            PEPSchedule.IntraDermal -> "Anti-rabies PEP - Intra-dermal"
            PEPSchedule.IntraMuscular -> "Anti-rabies PEP - Intra-muscular"
        }
    }

    @TypeConverter
    fun toPEPSchedule(value: String): PEPSchedule {
        return when (value) {
            "Anti-rabies PEP - Intra-dermal" -> PEPSchedule.IntraDermal
            "Anti-rabies PEP - Intra-muscular" -> PEPSchedule.IntraMuscular
            else -> PEPSchedule.IntraDermal
        }
    }
}

@Dao
interface HistoryDao {
    @Query("SELECT * FROM History ORDER BY long0 ASC")
    fun getDataFlow(): Flow<List<MgRecord>>

    @Query("SELECT * FROM History ORDER BY long0 ASC")
    suspend fun getData(): List<MgRecord>

    @Query("SELECT id0 FROM History WHERE long0= :long0")
    suspend fun getIdFromLong(long0: Long): Int

    @Query("DELETE FROM History WHERE id0 = :id0")
    suspend fun delete(id0: Int)

    @Query("DELETE FROM History")
    suspend fun deleteAll()

    @Insert
    suspend fun insert(vararg records: MgRecord): List<Long>

    @Query("UPDATE History SET isActive = :isActive WHERE id0= :id")
    suspend fun toggleActive(isActive: Boolean, id: Int)
}

@Database(
    entities = [MgRecord::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "com.ADGH.RabiesReminder_Registry_AIIMS_MG"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
