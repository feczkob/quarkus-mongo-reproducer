package com.ab.planner.nbt.domain.handler

import com.ab.planner.nbt.domain.model.nbt.Nbt
import com.ab.planner.nbt.domain.model.nbt.Section
import com.ab.planner.nbt.domain.model.nbt.Stage
import com.ab.planner.nbt.domain.model.nbt.StampingLocation

interface NbtHandler {
    suspend fun getNbt(): Nbt

    suspend fun getSection(id: Long): Section

    suspend fun getStage(id: Long): Stage

    suspend fun getStampingLocations(): List<StampingLocation>

    suspend fun getStampingLocation(id: Long): StampingLocation
}
