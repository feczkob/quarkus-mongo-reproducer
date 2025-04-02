package com.ab.planner.nbt.client.hike

import com.ab.planner.nbt.client.CacheService
import com.ab.planner.nbt.client.CacheType
import com.ab.planner.nbt.domain.exception.NotFoundException
import com.ab.planner.nbt.domain.handler.HikeHandler
import com.ab.planner.nbt.domain.mapAsync
import com.ab.planner.nbt.domain.model.plan.Hike
import com.ab.planner.nbt.domain.model.plan.HikeCollection
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import kotlinx.coroutines.coroutineScope
import java.util.UUID

@ApplicationScoped
class HikeService(
    private val cacheService: CacheService,
) : HikeHandler {
    // TODO #174 refactor duplication
    override suspend fun getHike(hikeId: UUID): Hike =
        coroutineScope {
            Log.info("Getting hike with id $hikeId")
            val cacheValue =
                cacheService.getValue(CacheType.HIKES, hikeId) ?: throw NotFoundException("Hike not found with id $hikeId")

            return@coroutineScope cacheValue.get() as Hike
        }

    override suspend fun getHikeCollection(hikeCollectionId: UUID): HikeCollection =
        coroutineScope {
            Log.info("Getting hike collection with id $hikeCollectionId")
            val cacheValue =
                cacheService.getValue(CacheType.HIKE_COLLECTIONS, hikeCollectionId) ?: throw NotFoundException(
                    "Hike collection not found with id $hikeCollectionId",
                )

            val hikeCollection = cacheValue.get() as com.ab.planner.nbt.client.hike.HikeCollection
            val hikeIds = hikeCollection.hikes

            val futureHikes = hikeIds.map { cacheService.getValue(CacheType.HIKES, it) }
            if (futureHikes.any { it == null }) {
                Log.debug("Invalidating hikeCollection with id $hikeCollectionId")
                hikeIds.forEach { hikeId ->
                    cacheService.invalidateCache(CacheType.HIKES, hikeId)
                }
                cacheService.invalidateCache(CacheType.HIKE_COLLECTIONS, hikeCollectionId)
                throw NotFoundException("Hike collection not found with id $hikeCollectionId")
            }

            val hikes = futureHikes.map { it?.get() as Hike }
            return@coroutineScope HikeCollection(hikes = hikes, id = hikeCollection.id)
        }

    override suspend fun saveHikeCollection(hikeCollection: HikeCollection): Unit =
        coroutineScope {
            Log.info("Saving hike collection with id ${hikeCollection.id}")
            val hikeIds = hikeCollection.hikes.map { it.id }
            cacheService.addCache(CacheType.HIKE_COLLECTIONS, hikeCollection.id, HikeCollection(hikeCollection.id, hikeIds))
            hikeCollection.hikes.mapAsync { hike ->
                cacheService.addCache(CacheType.HIKES, hike.id, hike)
            }
        }
}

data class HikeCollection(
    val id: UUID,
    val hikes: List<UUID>,
)
