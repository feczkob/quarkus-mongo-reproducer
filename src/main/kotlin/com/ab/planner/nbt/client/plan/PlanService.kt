package com.ab.planner.nbt.client.plan

import com.ab.planner.nbt.client.CacheService
import com.ab.planner.nbt.client.CacheType
import com.ab.planner.nbt.domain.exception.NotFoundException
import com.ab.planner.nbt.domain.handler.PlanHandler
import com.ab.planner.nbt.domain.mapAsync
import com.ab.planner.nbt.domain.model.plan.Plan
import com.ab.planner.nbt.domain.model.plan.PlanBetweenStampingLocations
import com.ab.planner.nbt.domain.model.plan.PlanCollection
import com.ab.planner.nbt.domain.model.plan.PlanFromHome
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import kotlinx.coroutines.coroutineScope
import java.util.UUID
import java.util.concurrent.CompletableFuture

@ApplicationScoped
class PlanService(
    private val cacheService: CacheService,
) : PlanHandler {
    override suspend fun getPlan(planId: UUID): Plan =
        coroutineScope {
            Log.info("Getting plan with id $planId")
            val cacheValue = cacheService.getValue(CacheType.PLANS, planId) ?: throw NotFoundException("Plan not found with id $planId")

            return@coroutineScope mapToDomain(cacheValue)
        }

    override suspend fun getPlanCollection(planCollectionId: UUID): PlanCollection =
        coroutineScope {
            Log.info("Getting plan collection with id $planCollectionId")
            val cacheValue =
                cacheService.getValue(CacheType.PLAN_COLLECTIONS, planCollectionId)
                    ?: throw NotFoundException("Plan collection not found with id $planCollectionId")

            val planCollection = cacheValue.get() as com.ab.planner.nbt.client.plan.PlanCollection
            val planIds = planCollection.plans

            val futurePlans = planIds.map { cacheService.getValue(CacheType.PLANS, it) }
            if (futurePlans.any { it == null }) {
                Log.debug("Invalidating planCollection with id $planCollectionId")
                planIds.forEach { planId ->
                    cacheService.invalidateCache(CacheType.PLANS, planId)
                }
                cacheService.invalidateCache(CacheType.PLAN_COLLECTIONS, planCollectionId)
                throw NotFoundException("Plan collection not found with id $planCollectionId")
            }

            val plans = futurePlans.map { mapToDomain(it) }
            return@coroutineScope PlanCollection(plans = plans, id = planCollection.id)
        }

    override suspend fun savePlanCollection(planCollection: PlanCollection) {
        Log.info("Saving plan collection with id ${planCollection.id}")
        val planIds = planCollection.plans.map { it.id }
        cacheService.addCache(
            CacheType.PLAN_COLLECTIONS,
            planCollection.id,
            PlanCollection(planCollection.id, planIds),
        )
        planCollection.plans.mapAsync { plan ->
            cacheService.addCache(CacheType.PLANS, plan.id, plan)
        }
    }

    private fun mapToDomain(futurePlan: CompletableFuture<Any>?) = try {
        futurePlan?.get() as PlanBetweenStampingLocations
    } catch (e: ClassCastException) {
        futurePlan?.get() as PlanFromHome
    }
}

data class PlanCollection(
    val id: UUID,
    val plans: List<UUID>,
)
