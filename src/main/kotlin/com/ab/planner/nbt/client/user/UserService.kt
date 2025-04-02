package com.ab.planner.nbt.client.user

import com.ab.planner.nbt.domain.handler.UserHandler
import com.ab.planner.nbt.domain.model.user.User
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserService : UserHandler {
    override suspend fun getUser(id: Long): User = User(listOf())
}
