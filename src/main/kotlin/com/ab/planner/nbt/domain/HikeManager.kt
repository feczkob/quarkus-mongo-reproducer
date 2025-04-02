package com.ab.planner.nbt.domain

import com.ab.planner.nbt.domain.handler.HikeHandler
import com.ab.planner.nbt.domain.model.HikeFactory
import com.ab.planner.nbt.domain.model.plan.HikeCollection
import com.ab.planner.nbt.domain.model.plan.HikeSpecification
import com.ab.planner.nbt.domain.model.user.User
import java.util.UUID

class HikeManager(
    private val hikeHandler: HikeHandler,
    private val hikeFactory: HikeFactory,
) {
    suspend fun createHikes(hikeSpecification: HikeSpecification): HikeCollection {
        val user = User(completedStages = emptyList())

        val hikeCollection = hikeFactory.buildHikeCollection(user, hikeSpecification)

        hikeHandler.saveHikeCollection(hikeCollection)

        return hikeCollection
    }

    suspend fun createHikesBetweenStampingLocations(stampingLocationAId: Long, stampingLocationBId: Long) : HikeCollection {
        val hikeCollection = hikeFactory.buildHikeCollection(stampingLocationAId, stampingLocationBId)

        hikeHandler.saveHikeCollection(hikeCollection)

        return hikeCollection
    }

    suspend fun getHikeCollection(hikeCollectionId: UUID): HikeCollection = hikeHandler.getHikeCollection(hikeCollectionId = hikeCollectionId)

    suspend fun getHike(hikeId: UUID) = hikeHandler.getHike(hikeId = hikeId)
}
