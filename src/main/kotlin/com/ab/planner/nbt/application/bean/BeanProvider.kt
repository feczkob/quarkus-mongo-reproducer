package com.ab.planner.nbt.application.bean

import com.ab.planner.nbt.domain.HikeManager
import com.ab.planner.nbt.domain.NbtManager
import com.ab.planner.nbt.domain.PlanManager
import com.ab.planner.nbt.domain.TransitManager
import com.ab.planner.nbt.domain.handler.HikeHandler
import com.ab.planner.nbt.domain.handler.ExampleHandler
import com.ab.planner.nbt.domain.handler.NbtHandler
import com.ab.planner.nbt.domain.handler.PlanHandler
import com.ab.planner.nbt.domain.handler.UserHandler
import com.ab.planner.nbt.domain.model.HikeFactory
import com.ab.planner.nbt.domain.model.PlanFactory
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.Produces

@ApplicationScoped
class BeanProvider {
    @ApplicationScoped
    @Produces
    fun nbtManager(
        nbtHandler: NbtHandler,
    ) = NbtManager(nbtHandler = nbtHandler)

    @ApplicationScoped
    @Produces
    fun travelManager(
        exampleHandler: ExampleHandler,
        planHandler: PlanHandler,
    ) = TransitManager(
        exampleHandler = exampleHandler,
        planHandler = planHandler,
    )

    @ApplicationScoped
    @Produces
    fun planManager(
        nbtHandler: NbtHandler,
        userHandler: UserHandler,
        planHandler: PlanHandler,
        hikeHandler: HikeHandler,
        planFactory: PlanFactory,
        hikeFactory: HikeFactory,
    ) = PlanManager(
        nbtHandler = nbtHandler,
        userHandler = userHandler,
        planHandler = planHandler,
        hikeHandler = hikeHandler,
        planFactory = planFactory,
        hikeFactory = hikeFactory,
    )

    @ApplicationScoped
    @Produces
    fun hikeManager(
        hikeHandler: HikeHandler,
        hikeFactory: HikeFactory,
    ) = HikeManager(
        hikeHandler = hikeHandler,
        hikeFactory = hikeFactory,
    )

    @ApplicationScoped
    @Produces
    fun planFactory(exampleHandler: ExampleHandler) =
        PlanFactory(
            exampleHandler = exampleHandler,
        )

    @ApplicationScoped
    @Produces
    fun hikeFactory(nbtHandler: NbtHandler) =
        HikeFactory(
            nbtHandler = nbtHandler,
        )
}
