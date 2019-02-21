package com.mirea.mongo.entity

import org.bson.types.ObjectId

class User(
        val name: String,
        val password: String, //bcrypted
        override val _id: ObjectId? = null
) : Persistent