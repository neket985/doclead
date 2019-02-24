package com.mirea.common

import com.mirea.mongo.entity.User
import io.ktor.auth.Principal
import org.bson.types.ObjectId

data class UserPrincipal(
        val name: String,
        val email: String,
        val id: ObjectId
) : Principal {
    fun toUserEmbedded() = User.UserEmbedded(name, email, id)
}