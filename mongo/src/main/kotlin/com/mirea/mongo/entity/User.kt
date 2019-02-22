package com.mirea.mongo.entity

import org.bson.types.ObjectId
import java.time.Instant

class User(
        val email: String,
        val password: String, //bcrypted
        val confirmed: Boolean, //email confirmation
        val confirmUid: String,
        val createdAt: Instant,
        override val _id: ObjectId? = null
) : Persistent{
    data class UserEmbedded(
            val email: String,
            val _id: ObjectId
    )

    fun toUserEmbedded() = UserEmbedded(email, _id!!)
}