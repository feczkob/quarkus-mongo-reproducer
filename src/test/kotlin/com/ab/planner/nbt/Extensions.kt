package com.ab.planner.nbt

import io.restassured.specification.RequestSpecification

fun RequestSpecification.queryParam(queryParam: Any): RequestSpecification {
    val fields = queryParam::class.java.declaredFields
    fields.forEach { field ->
        field.isAccessible = true
        val value = field.get(queryParam)
        if (value != null) {
            this.queryParam(field.name, value.toString())
        }
    }
    return this
}

fun <T> List<T>.deepEquals(other: List<T>) =
    size == other.size && asSequence()
        .mapIndexed { index, element -> element == other[index] }
        .all { it }
