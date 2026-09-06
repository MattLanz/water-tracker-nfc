package com.example.watertracker.di

import android.content.Context
import androidx.room.Room
import com.example.watertracker.data.local.AppDatabase
import com.example.watertracker.data.local.TagInfoDao
import com.example.watertracker.data.local.UserPrefsRepository
import com.example.watertracker.data.local.WaterIntakeDao
import com.example.watertracker.domain.repository.TagRepository
import com.example.watertracker.domain.repository.WaterIntakeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "water_tracker_db"
        ).build()
    }

    @Provides
    fun provideWaterIntakeDao(database: AppDatabase): WaterIntakeDao {
        return database.waterIntakeDao()
    }

    @Provides
    fun provideTagInfoDao(database: AppDatabase): TagInfoDao {
        return database.tagInfoDao()
    }

    @Provides
    @Singleton
    fun provideWaterIntakeRepository(dao: WaterIntakeDao): WaterIntakeRepository {
        return WaterIntakeRepository(dao)
    }

    @Provides
    @Singleton
    fun provideTagRepository(dao: TagInfoDao): TagRepository {
        return TagRepository(dao)
    }

    @Provides
    @Singleton
    fun provideUserPrefsRepository(@ApplicationContext context: Context): UserPrefsRepository {
        return UserPrefsRepository(context)
    }
}
