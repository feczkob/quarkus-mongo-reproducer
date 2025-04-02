package com.ab.planner.nbt.domain

import com.ab.planner.nbt.domain.handler.NbtHandler

class NbtManager(
    private val nbtHandler: NbtHandler,
) {
    suspend fun getStage(id: Long) = nbtHandler.getStage(id)

    suspend fun getNbt() = nbtHandler.getNbt()

    suspend fun getStampingLocations() = nbtHandler.getStampingLocations()
}
