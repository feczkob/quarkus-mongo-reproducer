package com.ab.planner.nbt.client.menetrendek

import io.quarkus.mongodb.panache.common.MongoEntity
import io.quarkus.mongodb.panache.kotlin.PanacheMongoEntity
import java.time.LocalDate

@MongoEntity(database = "nbtPlannerDB", collection = "transits")
class MongoTransit : PanacheMongoEntity() {
    lateinit var from: MongoStop
    lateinit var to: MongoStop
    lateinit var date: LocalDate
    lateinit var options: List<MongoTransitOption>
}

class MongoTransitOption {
    var ticketPrice: Int? = null
    lateinit var parts: List<MongoTransitPart>
}

class MongoTransitPart {
    lateinit var from: MongoStop
    lateinit var to: MongoStop
    lateinit var departureTime: String
    lateinit var arrivalTime: String
    lateinit var mode: String
    lateinit var transitNumber: String
}

class MongoStop {
    lateinit var settlementId: String
    lateinit var settlementName: String
    lateinit var stopId: String
    lateinit var stopName: String
}
