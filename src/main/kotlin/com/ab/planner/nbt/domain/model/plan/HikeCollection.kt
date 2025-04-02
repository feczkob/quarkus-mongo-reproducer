package com.ab.planner.nbt.domain.model.plan

import java.util.UUID

class HikeCollection(
    val hikes: List<Hike>,
    val id: UUID = UUID.randomUUID(),
)
