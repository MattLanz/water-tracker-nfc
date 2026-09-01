package com.example.watertracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TagInfoDao {
    @Insert
    suspend fun insert(tagInfo: TagInfoEntity)

    @Query("SELECT * FROM tag_info WHERE uid = :uid LIMIT 1")
    suspend fun getByUid(uid: String): TagInfoEntity?
}
