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

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideDatabaseHandler(database: AppDatabase): DatabaseHandler {
        return DatabaseHandler(database)
    }

    @Provides
    @Singleton
    fun provideReminderRepository(databaseHandler: DatabaseHandler): ReminderRepository {
        return ReminderRepository(databaseHandler)
    }
}
