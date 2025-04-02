package com.ab.planner.nbt.domain.model.plan

import com.ab.planner.nbt.domain.model.nbt.Stage

abstract class HikeSpecification(
    private val preferredSections: Set<Long>,
) {
    init {
        require(preferredSections.isNotEmpty()) { "preferredSections must not be empty" }
    }

    abstract fun canContinueWith(hike: Hike): Boolean

    abstract fun canAccept(hike: Hike): Boolean

    fun isPreferredSectionsValid(sectionIds: List<Long>): Boolean = preferredSections.all { it in sectionIds }

    fun canAccept(stage: Stage): Boolean = stage.sectionId in preferredSections
}
