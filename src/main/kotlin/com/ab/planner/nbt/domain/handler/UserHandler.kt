package com.ab.planner.nbt.domain.handler

import com.ab.planner.nbt.domain.model.user.User

fun interface UserHandler {

    suspend fun getUser(id: Long): User
}