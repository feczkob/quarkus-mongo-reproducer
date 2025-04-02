package com.ab.planner.nbt.domain.handler

import com.ab.planner.nbt.domain.model.plan.Plan
import com.ab.planner.nbt.domain.model.plan.PlanCollection
import java.util.UUID

interface PlanHandler {
    suspend fun getPlan(planId: UUID): Plan

    suspend fun getPlanCollection(planCollectionId: UUID): PlanCollection

    suspend fun savePlanCollection(planCollection: PlanCollection)
}
