package com.example.watertracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tag_info")
data class TagInfoEntity(
    @PrimaryKey val uid: String,
    val capacityLiters: Float
)
