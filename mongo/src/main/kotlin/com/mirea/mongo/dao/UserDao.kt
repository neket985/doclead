package com.mirea.mongo.dao

import com.mirea.mongo.entity.User
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.eq

class UserDao(mongoDb: MongoDatabase) : CommonDao<User>(mongoDb, User::class) {

    fun getByUsername(name: String) = findOne(User::name eq name)
}