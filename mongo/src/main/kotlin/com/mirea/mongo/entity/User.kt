package com.mirea.mongo.entity

import org.bson.types.ObjectId
import java.time.Instant

class User(
        val email: String,
        val name: String, //todo в имени не должно быть символа @ , что бы не было конфликтов с email
        val password: String, //bcrypted
        val confirmed: Boolean, //email confirmation
        val confirmUid: String,
        val createdAt: Instant,
        override var _id: ObjectId? = null
) : Persistent {
    data class UserEmbedded(
            val name: String,
            val email: String,
            val _id: ObjectId
    ) {
        override fun equals(other: Any?) =
                if (other is UserEmbedded) {
                    this._id == other._id
                } else super.equals(other)

        override fun hashCode() = _id.hashCode()
    }

    fun toUserEmbedded() = UserEmbedded(name, email, _id!!)
}