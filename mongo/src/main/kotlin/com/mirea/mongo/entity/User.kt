package com.mirea.mongo.entity

import org.bson.types.ObjectId

class User(
        val email: String,
        val password: String, //bcrypted
        val confirmed: Boolean, //email confirmation
        val confirmUid: String,
        override val _id: ObjectId? = null
) : Persistent