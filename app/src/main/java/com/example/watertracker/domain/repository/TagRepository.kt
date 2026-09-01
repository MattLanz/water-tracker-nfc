package com.example.watertracker.domain.repository

import com.example.watertracker.data.local.TagInfoDao
import com.example.watertracker.data.local.TagInfoEntity

class TagRepository(private val dao: TagInfoDao) {
    suspend fun getTagInfo(uid: String): TagInfoEntity? = dao.getByUid(uid)
    suspend fun registerTag(uid: String, capacityLiters: Float) {
        dao.insert(TagInfoEntity(uid = uid, capacityLiters = capacityLiters))
    }
}
