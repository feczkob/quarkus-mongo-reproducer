package com.ab.planner.nbt.domain

import com.ab.planner.nbt.domain.handler.HikeHandler
import com.ab.planner.nbt.domain.handler.NbtHandler
import com.ab.planner.nbt.domain.handler.PlanHandler
import com.ab.planner.nbt.domain.handler.UserHandler
import com.ab.planner.nbt.domain.model.HikeFactory
import com.ab.planner.nbt.domain.model.PlanFactory
import com.ab.planner.nbt.domain.model.plan.HikeCollection
import com.ab.planner.nbt.domain.model.plan.HikeSpecification
import com.ab.planner.nbt.domain.model.plan.Plan
import com.ab.planner.nbt.domain.model.plan.PlanCollection
import com.ab.planner.nbt.domain.model.plan.PlanSpecification
import com.ab.planner.nbt.domain.model.travel.Stop
import io.quarkus.logging.Log
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.UUID

class PlanManager(
    private val nbtHandler: NbtHandler,
    private val userHandler: UserHandler,
    private val planHandler: PlanHandler,
    private val hikeHandler: HikeHandler,
    private val planFactory: PlanFactory,
    private val hikeFactory: HikeFactory,
) {
    // TODO #155 remove
    suspend fun makePlansBetweenStampingLocations(
        hikeSpecification: HikeSpecification,
        planSpecification: PlanSpecification,
    ): PlanCollection =
        coroutineScope {
            val nbt = async { nbtHandler.getNbt() }
            val user = async { userHandler.getUser(0) }

            val hikes = nbt.await().createHikes(hikeSpecification, user.await())
            val hikeCollection = HikeCollection(hikes)

            val otherHikeCollection = hikeFactory.buildHikeCollection(user.await(), hikeSpecification)

            if (hikeCollection.hikes.size != otherHikeCollection.hikes.size) {
                Log.warn(
                    "There's some error in the buildHikeCollection function! Hikes from Stage:" +
                        "${hikeCollection.hikes}, hikes from HikeFactory: ${otherHikeCollection.hikes}",
                )
            }

            val planCollection =
                planFactory
                    .buildPlanCollectionBetweenStampingLocations(hikeCollection, planSpecification.travelDate)
                    .complyWith(planSpecification)

            planHandler.savePlanCollection(planCollection)
            return@coroutineScope planCollection
        }

    suspend fun makePlansBetweenStampingLocations(
        hikeCollectionId: UUID,
        planSpecification: PlanSpecification,
    ): PlanCollection =
        coroutineScope {
            val hikeCollection = hikeHandler.getHikeCollection(hikeCollectionId)

            val planCollection =
                planFactory
                    .buildPlanCollectionBetweenStampingLocations(hikeCollection, planSpecification.travelDate)
                    .complyWith(planSpecification)

            planHandler.savePlanCollection(planCollection)
            return@coroutineScope planCollection
        }

    suspend fun makePlansFromHome(
        hikeCollectionId: UUID,
        planSpecification: PlanSpecification,
        home: Stop,
    ): PlanCollection =
        coroutineScope {
            val hikeCollection = hikeHandler.getHikeCollection(hikeCollectionId)

            val planCollection =
                planFactory
                    .buildPlanCollectionFromHome(hikeCollection, planSpecification.travelDate, home)
                    .complyWith(planSpecification)

            planHandler.savePlanCollection(planCollection)
            return@coroutineScope planCollection
        }

    suspend fun getPlan(planId: UUID): Plan = planHandler.getPlan(planId)
}
