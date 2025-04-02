package com.ab.planner.nbt.domain.model.plan

class DistanceHike(
    private val minHikeLength: Double,
    private val maxHikeLength: Double,
    preferredSections: Set<Long>,
) : HikeSpecification(preferredSections) {
    init {
        check(minHikeLength <= maxHikeLength) { "minHikeLength must not be greater than maxHikeLength" }
    }

    override fun canContinueWith(hike: Hike): Boolean = hike.totalLength < maxHikeLength

    override fun canAccept(hike: Hike): Boolean = hike.totalLength in minHikeLength..maxHikeLength
}
