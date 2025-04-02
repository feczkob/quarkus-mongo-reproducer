package com.ab.planner.nbt.domain.model.plan

import kotlin.time.Duration

class TimeHike(
    private val minHikeTime: Duration,
    private val maxHikeTime: Duration,
    preferredSections: Set<Long>,
) : HikeSpecification(preferredSections) {
    init {
        check(minHikeTime <= maxHikeTime) { "minHikeTime must be smaller than maxHikeTime" }
    }

    override fun canContinueWith(hike: Hike): Boolean = hike.totalHikeTime < maxHikeTime

    override fun canAccept(hike: Hike) = hike.totalHikeTime in minHikeTime..maxHikeTime
}
