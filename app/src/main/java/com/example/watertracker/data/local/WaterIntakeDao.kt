package com.example.watertracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WaterIntakeDao {
    @Insert
    suspend fun insert(intake: WaterIntakeEntity)

    @Query("SELECT * FROM water_intake ORDER BY timestamp DESC")
    suspend fun getAll(): List<WaterIntakeEntity>
}
