package com.example.watertracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WaterIntakeEntity::class, TagInfoEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun waterIntakeDao(): WaterIntakeDao
    abstract fun tagInfoDao(): TagInfoDao
}
