package com.mirea.mongo.entity

import org.bson.types.ObjectId

interface Persistent {
    var _id: ObjectId?
}
