package com.mirea.mongo.entity

import org.bson.types.ObjectId

interface Persistent {
    val _id: ObjectId?
}
