package com.ab.planner.nbt.domain.handler

import com.ab.planner.nbt.domain.model.plan.Hike
import com.ab.planner.nbt.domain.model.plan.HikeCollection
import java.util.UUID

interface HikeHandler {
    suspend fun getHike(hikeId: UUID): Hike

    suspend fun getHikeCollection(hikeCollectionId: UUID): HikeCollection

    suspend fun saveHikeCollection(hikeCollection: HikeCollection)
}
