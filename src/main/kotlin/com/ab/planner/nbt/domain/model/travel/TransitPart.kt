package com.ab.planner.nbt.domain.model.travel

import java.time.LocalDateTime

class TransitPart(
    val from: Stop,
    val to: Stop,
    val departureTime: LocalDateTime,
    val arrivalTime: LocalDateTime,
    val mode: TransitMode,
    val transitNumber: String,
) {
    init {
        check(departureTime.isBefore(arrivalTime)) { "departureTime must be before arrivalTime" }
    }
}
