package com.ab.planner.nbt.domain.handler

import com.ab.planner.nbt.domain.model.travel.Stop
import com.ab.planner.nbt.domain.model.travel.Transit
import java.time.LocalDate

interface ExampleHandler {
    suspend fun getStops(stopName: String): List<Stop>

    suspend fun getTransit(
        from: Stop,
        to: Stop,
        travelDate: LocalDate,
    ): Transit
}
