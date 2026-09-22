package com.adgh.rabiesreminder.di

import android.content.Context
import com.adgh.rabiesreminder.AppDatabase
import com.adgh.rabiesreminder.DatabaseHandler
import com.adgh.rabiesreminder.ReminderRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency injection module providing database and repository singletons via Hilt.
 *
 * Installed in [SingletonComponent] to ensure database dependencies live throughout
 * the application lifecycle.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides the application-wide singleton instance of the Room [AppDatabase].
     *
     * @param context Application context injected via [@ApplicationContext][ApplicationContext].
     * @return The singleton [AppDatabase] instance.
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    /**
     * Provides the application-wide singleton instance of [DatabaseHandler].
     *
     * @param database The [AppDatabase] instance.
     * @return The singleton [DatabaseHandler] wrapper.
     */
    @Provides
    @Singleton
    fun provideDatabaseHandler(database: AppDatabase): DatabaseHandler {
        return DatabaseHandler(database)
    }

    /**
     * Provides the application-wide singleton instance of [ReminderRepository].
     *
     * @param databaseHandler The [DatabaseHandler] instance.
     * @return The singleton [ReminderRepository] instance.
     */
    @Provides
    @Singleton
    fun provideReminderRepository(databaseHandler: DatabaseHandler): ReminderRepository {
        return ReminderRepository(databaseHandler)
    }
}

