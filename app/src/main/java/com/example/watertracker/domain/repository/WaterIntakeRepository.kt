package com.example.watertracker.domain.repository

import com.example.watertracker.data.local.WaterIntakeDao
import com.example.watertracker.data.local.WaterIntakeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WaterIntakeRepository(private val dao: WaterIntakeDao) {
    suspend fun addIntake(timestamp: Long, liters: Float) {
        dao.insert(WaterIntakeEntity(timestamp = timestamp, liters = liters))
    }

    suspend fun getAllIntakes(): List<WaterIntakeEntity> = dao.getAll()
}
