package com.ab.planner.nbt.client.menetrendek

import io.quarkus.logging.Log
import io.quarkus.mongodb.panache.kotlin.PanacheMongoRepository
import io.quarkus.scheduler.Scheduled
import jakarta.enterprise.context.ApplicationScoped
import java.time.LocalDate

@ApplicationScoped
class TransitRepository : PanacheMongoRepository<MongoTransit> {
    fun findTransit(
        from: MongoStop,
        to: MongoStop,
        date: LocalDate,
    ): MongoTransit? {
        val fromSettlementId = from.settlementId
        val fromStopId = from.stopId
        val toSettlementId = to.settlementId
        val toStopId = to.stopId

        return find(
            "from.settlementId = ?1 and from.stopId = ?2 and to.settlementId = ?3 and to.stopId = ?4 and date = ?5",
            fromSettlementId,
            fromStopId,
            toSettlementId,
            toStopId,
            date,
        ).firstResult()
    }

    @Scheduled(cron = "0 0 3 * * ?")
    internal fun deleteOldTransits() {
        val numOfOldTransits = delete("{'date': { '\$lt': ?1 }}", LocalDate.now())
        Log.info("Deleted $numOfOldTransits old transit(s)")
    }
}
