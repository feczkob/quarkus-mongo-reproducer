package com.ab.planner.nbt.client

import io.quarkus.cache.Cache
import io.quarkus.cache.CacheName
import io.quarkus.cache.CaffeineCache
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped
import java.util.concurrent.CompletableFuture

// * https://quarkus.io/guides/cache#programmatic-api
// * https://github.com/quarkusio/quarkus/issues/23746
// * https://github.com/quarkusio/quarkus/issues/21592
// * caching does not yet work properly with kotlin suspend functions

@ApplicationScoped
class CacheService(
    @CacheName(CacheTypeConstants.PLANS)
    private val plansCache: Cache,
    @CacheName(CacheTypeConstants.PLAN_COLLECTIONS)
    private val planCollectionsCache: Cache,
    @CacheName(CacheTypeConstants.HIKES)
    private val hikesCache: Cache,
    @CacheName(CacheTypeConstants.HIKE_COLLECTIONS)
    private val hikeCollectionsCache: Cache,
) {
    private fun getCache(type: CacheType): Cache =
        when (type) {
            CacheType.PLANS -> plansCache
            CacheType.PLAN_COLLECTIONS -> planCollectionsCache
            CacheType.HIKES -> hikesCache
            CacheType.HIKE_COLLECTIONS -> hikeCollectionsCache
        }

    fun allCacheKeys(type: CacheType): Set<Any> = getCache(type).`as`(CaffeineCache::class.java).keySet()

    fun addCache(
        type: CacheType,
        key: Any,
        value: Any,
    ) {
        getCache(type).`as`(CaffeineCache::class.java).put(key, CompletableFuture.completedFuture(value))
    }

    fun getValue(
        type: CacheType,
        key: Any,
    ): CompletableFuture<Any>? = getCache(type).`as`(CaffeineCache::class.java).getIfPresent(key)

    suspend fun invalidateCache(
        type: CacheType,
        key: Any,
    ) = getCache(type).`as`(CaffeineCache::class.java).invalidate(key).awaitSuspending()
}

enum class CacheType {
    PLANS,
    PLAN_COLLECTIONS,
    HIKES,
    HIKE_COLLECTIONS,
}

private object CacheTypeConstants {
    const val PLANS = "plans"
    const val PLAN_COLLECTIONS = "planCollections"
    const val HIKES = "hikes"
    const val HIKE_COLLECTIONS = "hikeCollections"
}
