package com.ab.planner.nbt.client.nbt

import com.ab.planner.nbt.domain.exception.NotFoundException
import com.ab.planner.nbt.domain.handler.NbtHandler
import com.ab.planner.nbt.domain.model.nbt.Nbt
import com.ab.planner.nbt.domain.model.nbt.Section
import com.ab.planner.nbt.domain.model.nbt.Stage
import com.ab.planner.nbt.domain.model.nbt.StampingLocation
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Named

@ApplicationScoped
class NbtService(
    @Named("stampingLocations")
    private val stampingLocations: Map<Long, StampingLocation>,
    @Named("stages")
    private val stages: Map<Long, Stage>,
) : NbtHandler {
    private val sections: Map<Long, Section>
        get() {
            return stages.values
                .groupBy { it.sectionId }
                .map { (sectionId, stages) ->
                    Section(id = sectionId, stages = stages)
                }.associateBy { it.id }
        }

    override suspend fun getNbt(): Nbt = Nbt(sections.values.toList().sortedBy { it.id })

    override suspend fun getStage(id: Long): Stage = stages[id] ?: throw NotFoundException("Stage not found with id: $id")

    override suspend fun getStampingLocations(): List<StampingLocation> = stampingLocations.values.toList().sortedBy { it.id }

    override suspend fun getSection(id: Long): Section = sections[id] ?: throw NotFoundException("Section not found with id: $id")

    override suspend fun getStampingLocation(id: Long): StampingLocation =
        stampingLocations[id] ?: throw NotFoundException("Stamping location not found with id: $id")
}
