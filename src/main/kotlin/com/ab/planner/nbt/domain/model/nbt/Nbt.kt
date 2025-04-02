package com.ab.planner.nbt.domain.model.nbt

import com.ab.planner.nbt.domain.mapAsync
import com.ab.planner.nbt.domain.model.plan.Hike
import com.ab.planner.nbt.domain.model.plan.HikeSpecification
import com.ab.planner.nbt.domain.model.user.User

class Nbt(
    val sections: List<Section>,
) {
    init {
        check(sections.isNotEmpty()) { "Nbt must contain at least one section" }
    }

    val stages: List<Stage> by lazy { sections.flatMap { it.stages } }

    // TODO #155 remove
    suspend fun createHikes(
        hikeSpecification: HikeSpecification,
        user: User,
    ): List<Hike> {
        validatePreferredSections(hikeSpecification)

        val hikes =
            stages
                .filter { stage -> !user.hasCompleted(stage) }
                .filter { stage -> hikeSpecification.canAccept(stage) }
                .let { it.mapAsync { stage -> stage.createHikes(it, hikeSpecification) } }
                .flatten()
                .filter { it.isAccessible }

        if (hikes.isEmpty()) {
            throw NoSuchElementException("No hikes found for the given specification")
        }

        return hikes
    }

    fun validatePreferredSections(hikeSpecification: HikeSpecification) {
        val sectionIds = sections.map { section -> section.id }
        require(hikeSpecification.isPreferredSectionsValid(sectionIds)) {
            "Each preferred section must be in the range of [${sectionIds.min()}, ${sectionIds.max()}]"
        }
    }
}
